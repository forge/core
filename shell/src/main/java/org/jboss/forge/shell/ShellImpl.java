/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell;

import static org.mvel2.DataConversion.addConversionHandler;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.buffers.JLineScreenBuffer;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PromptTypeConverter;
import org.jboss.forge.shell.command.convert.BooleanConverter;
import org.jboss.forge.shell.command.convert.DependencyIdConverter;
import org.jboss.forge.shell.command.convert.FileConverter;
import org.jboss.forge.shell.command.convert.URLConverter;
import org.jboss.forge.shell.command.fshparser.FSHRuntime;
import org.jboss.forge.shell.completer.CompletedCommandHolder;
import org.jboss.forge.shell.completer.OptionAwareCompletionHandler;
import org.jboss.forge.shell.completer.PluginCommandCompleter;
import org.jboss.forge.shell.console.jline.Terminal;
import org.jboss.forge.shell.console.jline.TerminalFactory;
import org.jboss.forge.shell.console.jline.console.ConsoleReader;
import org.jboss.forge.shell.console.jline.console.completer.AggregateCompleter;
import org.jboss.forge.shell.console.jline.console.completer.Completer;
import org.jboss.forge.shell.console.jline.console.history.MemoryHistory;
import org.jboss.forge.shell.events.AcceptUserInput;
import org.jboss.forge.shell.events.PreShutdown;
import org.jboss.forge.shell.events.Shutdown;
import org.jboss.forge.shell.events.Startup;
import org.jboss.forge.shell.exceptions.AbortedException;
import org.jboss.forge.shell.exceptions.CommandExecutionException;
import org.jboss.forge.shell.exceptions.CommandParserException;
import org.jboss.forge.shell.exceptions.PluginExecutionException;
import org.jboss.forge.shell.exceptions.ShellExecutionException;
import org.jboss.forge.shell.plugins.builtin.Echo;
import org.jboss.forge.shell.project.CurrentProject;
import org.jboss.forge.shell.spi.CommandInterceptor;
import org.jboss.forge.shell.spi.TriggeredAction;
import org.jboss.forge.shell.util.Files;
import org.jboss.forge.shell.util.GeneralUtils;
import org.jboss.forge.shell.util.JavaPathspecParser;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.mvel2.ConversionHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@ApplicationScoped
public class ShellImpl extends AbstractShellPrompt implements Shell
{
   static final String PROP_FORGE_CONFIG_DIR = "FORGE_CONFIG_DIR";
   static final String PROP_PROMPT = "PROMPT";
   static final String PROP_PROMPT_NO_PROJ = "PROMPT_NOPROJ";

   static final String DEFAULT_PROMPT = "[\\c{green}$PROJECT_NAME\\c] \\c{blue}\\W\\c \\c{green}\\$\\c ";
   static final String DEFAULT_PROMPT_NO_PROJ = "[\\c{red}no project\\c] \\c{blue}\\W\\c \\c{red}\\$\\c ";

   public static final String PROP_DEFAULT_PLUGIN_REPO = "DEFAULT_PLUGIN_REPO";
   static final String DEFAULT_PLUGIN_REPO = "https://raw.github.com/forge/plugin-repository/master/repository.yaml";

   static final String PROP_VERBOSE = "VERBOSE";
   static final String PROP_EXCEPTION_HANDLING = "EXCEPTION_HANDLING";

   static final String PROP_IGNORE_EOF = "IGNOREEOF";
   static final int DEFAULT_IGNORE_EOF = 1;

   public static final String FORGE_CONFIG_DIR = System.getProperty("user.home") + "/.forge/";
   public static final String FORGE_COMMAND_HISTORY_FILE = "cmd_history";
   public static final String FORGE_CONFIG_FILE = "config";
   public static final String OFFLINE_FLAG = "OFFLINE";

   @Inject
   @Parameters
   private List<String> parameters;

   @Inject
   private BeanManager manager;

   @Inject
   private Event<Shutdown> shutdown;

   @Inject
   private CurrentProject projectContext;

   @Inject
   ResourceFactory resourceFactory;

   private Resource<?> lastResource;

   @Inject
   private FSHRuntime fshRuntime;

   @Inject
   PromptTypeConverter promptTypeConverter;

   @Inject
   private CompletedCommandHolder commandHolder;

   @Inject
   private ForgeEnvironment environment;

   private ConsoleReader reader;
   private Completer completer;

   private boolean pretend = false;
   private boolean exitRequested = false;

   private InputStream inputStream;
   private OutputStream outputStream;
   private OutputStream historyOutstream;

   private BufferManager screenBuffer;

   private enum BufferingMode
   {
      Direct, Buffering
   }


   private BufferingMode bufferingMode = BufferingMode.Direct;

   private final boolean colorEnabled = Boolean.getBoolean("forge.shell.colorEnabled");

   private final ConversionHandler resourceConversionHandler = new ConversionHandler()
   {
      @Override
      @SuppressWarnings("rawtypes")
      public Resource[] convertFrom(final Object obl)
      {
         return GeneralUtils.parseSystemPathspec(resourceFactory, lastResource, getCurrentResource(),
                  obl instanceof String[] ? (String[]) obl : new String[]{obl.toString()});
      }

      @SuppressWarnings("rawtypes")
      @Override
      public boolean canConvertFrom(final Class aClass)
      {
         return true;
      }
   };

   private final ConversionHandler javaResourceConversionHandler = new ConversionHandler()
   {
      @Override
      public JavaResource[] convertFrom(final Object obj)
      {
         if (getCurrentProject().hasFacet(JavaSourceFacet.class))
         {
            String[] strings = obj instanceof String[] ? (String[]) obj : new String[]{obj.toString()};
            List<Resource<?>> resources = new ArrayList<Resource<?>>();
            for (String string : strings)
            {
               resources.addAll(new JavaPathspecParser(getCurrentProject().getFacet(JavaSourceFacet.class),
                        string).resolve());
            }

            List<JavaResource> filtered = new ArrayList<JavaResource>();
            for (Resource<?> resource : resources)
            {
               if (resource instanceof JavaResource)
               {
                  filtered.add((JavaResource) resource);
               }
            }

            JavaResource[] result = new JavaResource[filtered.size()];
            result = filtered.toArray(result);
            return result;
         }
         else
            return null;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public boolean canConvertFrom(final Class aClass)
      {
         return true;
      }
   };

   private int numEOF = 0;
   private boolean executing;

   @Inject
   private ShellConfig shellConfig;

   @Inject
   private Instance<CommandInterceptor> commandInterceptors;

   @Inject
   private Instance<TriggeredAction> triggeredActions;

   void init(@Observes final Startup event, final PluginCommandCompleter pluginCompleter) throws Exception
   {
      BooleanConverter booleanConverter = new BooleanConverter();

      addConversionHandler(boolean.class, booleanConverter);
      addConversionHandler(Boolean.class, booleanConverter);
      addConversionHandler(File.class, new FileConverter());
      addConversionHandler(Dependency.class, new DependencyIdConverter());
      addConversionHandler(URL.class, new URLConverter());

      addConversionHandler(JavaResource[].class, javaResourceConversionHandler);
      addConversionHandler(JavaResource.class, new ConversionHandler()
      {

         @Override
         public Object convertFrom(final Object obj)
         {
            JavaResource[] res = (JavaResource[]) javaResourceConversionHandler.convertFrom(obj);
            if (res.length > 1)
            {
               throw new RuntimeException("ambiguous paths");
            }
            else if (res.length == 0)
            {
               if (getCurrentProject().hasFacet(JavaSourceFacet.class))
               {
                  JavaSourceFacet java = getCurrentProject().getFacet(JavaSourceFacet.class);
                  try
                  {
                     JavaResource resource = java.getJavaResource(obj.toString());
                     return resource;
                  }
                  catch (FileNotFoundException e)
                  {
                     throw new RuntimeException(e);
                  }
               }
               return null;
            }
            else
            {
               return res[0];
            }
         }

         @Override
         @SuppressWarnings("rawtypes")
         public boolean canConvertFrom(final Class type)
         {
            return javaResourceConversionHandler.canConvertFrom(type);
         }
      });
      addConversionHandler(Resource[].class, resourceConversionHandler);
      addConversionHandler(Resource.class, new ConversionHandler()

      {
         @Override
         public Object convertFrom(final Object o)
         {
            Resource<?>[] res = (Resource<?>[]) resourceConversionHandler.convertFrom(o);
            if (res.length > 1)
            {
               throw new RuntimeException("ambiguous paths");
            }
            else if (res.length == 0)
            {
               return ResourceUtil.parsePathspec(resourceFactory, getCurrentResource(), o.toString()).get(0);
            }
            else
            {
               return res[0];
            }
         }

         @Override
         @SuppressWarnings("rawtypes")
         public boolean canConvertFrom(final Class aClass)
         {
            return resourceConversionHandler.canConvertFrom(aClass);
         }
      });


      configureOSTerminal();
      initReaderAndStreams();
      initParameters();

      if (event.isRestart())
      {
         // suppress the MOTD if this is a restart.
         environment.setProperty("NO_MOTD", true);
      }
      else
      {
         environment.setProperty("NO_MOTD", false);
      }

      environment.setProperty("OS_NAME", OSUtils.getOsName());
      environment.setProperty(PROP_FORGE_CONFIG_DIR, FORGE_CONFIG_DIR);
      environment.setProperty(PROP_PROMPT, "> ");
      environment.setProperty(PROP_PROMPT_NO_PROJ, "> ");

      shellConfig.loadConfig(this);

      if (Boolean.getBoolean("forge.offline") == true)
      {
         environment.setProperty(OFFLINE_FLAG, true);
      }
      else
      {
         environment.setProperty(OFFLINE_FLAG, false);
      }

      initCompleters(pluginCompleter);
      initSignalHandlers();

      /*
       * Do this last so that we don't fire off plugin events before the shell has booted
       * (Causing all kinds of wonderful issues)
       */
      projectContext.setCurrentResource(resourceFactory.getResourceFrom(event.getWorkingDirectory()));
      environment.setProperty("CWD", getCurrentDirectory().getFullyQualifiedName());
      environment.setProperty("SHELL", this);
   }

   private void initSignalHandlers()
   {
      try
      {
         // check to see if we have something to work with.
         Class.forName("sun.misc.SignalHandler");
         SigHandler.init(this);
      }
      catch (ClassNotFoundException e)
      {
         // signal trapping not supported. Oh well, switch to a Sun-based JVM, loser!
      }
   }

   @Override
   public void writeToHistory(final String command)
   {
      try
      {
         for (int i = 0; i < command.length(); i++)
         {
            historyOutstream.write(command.charAt(i));
         }
         historyOutstream.write('\n');
         historyOutstream.flush();
      }
      catch (IOException e)
      {
      }
   }

   @Override
   public void setHistoryOutputStream(final OutputStream stream)
   {
      historyOutstream = stream;
      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               historyOutstream.flush();
               historyOutstream.close();
            }
            catch (Exception e)
            {
            }
         }
      });
   }

   @Override
   public void setHistory(final List<String> lines)
   {
      MemoryHistory history = new MemoryHistory();

      for (String line : lines)
      {
         history.add(line);
      }

      reader.setHistory(history);
   }

   private void initCompleters(final PluginCommandCompleter pluginCompleter)
   {
      List<Completer> completers = new ArrayList<Completer>();
      completers.add(pluginCompleter);

      completer = new AggregateCompleter(completers);
      this.reader.addCompleter(completer);
      this.reader.setCompletionHandler(new OptionAwareCompletionHandler(commandHolder, this));
   }

   private void initReaderAndStreams() throws IOException
   {


      if (inputStream == null)
      {
         inputStream = System.in;
      }
      if (outputStream == null)
      {
         outputStream = System.out;
      }

      Terminal terminal;
      if (Boolean.getBoolean("forge.compatibility.IDE"))
      {
         terminal = new IdeTerminal();
      }
      else if (OSUtils.isWindows())
      {
         final OutputStream ansiOut = AnsiConsole.wrapOutputStream(outputStream);
         final OutputStreamWriter writer = new OutputStreamWriter(ansiOut, System.getProperty(
                  "WindowsTerminal.output.encoding", System.getProperty("file.encoding")));

         outputStream = new OutputStream()
         {
            @Override
            public void write(int b) throws IOException
            {
               writer.write(b);
            }
         };

         TerminalFactory.configure(TerminalFactory.Type.WINDOWS);
         terminal = TerminalFactory.get();
      }
      else
      {
         terminal = TerminalFactory.get();
      }

      this.screenBuffer = new JLineScreenBuffer(terminal, outputStream);
      this.reader = new ConsoleReader(inputStream, screenBuffer, null, terminal);
      this.reader.setHistoryEnabled(true);
      this.reader.setBellEnabled(false);

      for (TriggeredAction action : triggeredActions)
      {
         this.reader.addTriggeredAction(action.getTrigger(), action.getListener());
      }
   }

   private void initParameters()
   {
      environment.setProperty(PROP_VERBOSE, String.valueOf(parameters.contains("--verbose")));
      environment.setProperty(PROP_EXCEPTION_HANDLING,
               String.valueOf(parameters.contains("--disableExceptionHandlers") != true));

      if (parameters.contains("--pretend"))
      {
         pretend = true;
      }

      if ((parameters != null) && !parameters.isEmpty())
      {
         // this is where we will initialize other parameters... e.g. accepting
         // a path
      }
   }

   void teardown(@Observes final Shutdown shutdown, final Event<PreShutdown> preShutdown)
   {
      preShutdown.fire(new PreShutdown(shutdown.getStatus()));
      exitRequested = true;
   }

   void doShell(@Observes final AcceptUserInput event) throws Exception
   {
      String line;
      reader.setPrompt(getPrompt());
      while (!exitRequested)
      {
         try
         {
            line = readLine();

            if (line != null)
            {
               if (!"".equals(line.trim()))
               {
                  writeToHistory(line);
                  execute(line);
                  flushBuffer();
               }
               reader.setPrompt(getPrompt());
            }

         }
         catch (Exception e)
         {
            handleException(e);
         }
      }
   }

   private void handleException(final Exception original) throws Exception
   {
      if (!isExceptionHandlingEnabled())
      {
         Throwable root = original;
         while ((root.getCause() != null) && !root.getCause().equals(root))
         {
            root = root.getCause();
         }
         if (root instanceof Exception)
            throw (Exception) root;
         else
            throw new RuntimeException(root);
      }

      try
      {
         // unwrap any aborted exceptions
         Throwable cause = original;
         while (cause != null)
         {
            if (cause instanceof AbortedException)
               throw (AbortedException) cause;

            cause = cause.getCause();
         }

         throw original;
      }
      catch (AbortedException e)
      {
         ShellMessages.info(this, "Aborted.");
         if (isVerbose())
         {
            e.printStackTrace();
         }
      }
      catch (CommandExecutionException e)
      {
         ShellMessages.error(this, formatSourcedError(e.getCommand()) + e.getMessage());
         if (isVerbose())
         {
            e.printStackTrace();
         }
      }
      catch (CommandParserException e)
      {
         ShellMessages.error(this, formatSourcedError(e.getCommand()) + e.getMessage());
         if (isVerbose())
         {
            e.printStackTrace();
         }
      }
      catch (PluginExecutionException e)
      {
         ShellMessages.error(this, formatSourcedError(e.getPlugin()) + e.getMessage());
         if (isVerbose())
         {
            e.printStackTrace();
         }
      }
      catch (ShellExecutionException e)
      {
         ShellMessages.error(this, e.getMessage());
         if (isVerbose())
         {
            e.printStackTrace();
         }
      }
      catch (Exception e)
      {
         if (!isVerbose())
         {
            ShellMessages.error(this, "Exception encountered: " + e.getMessage()
                     + " (type \"set VERBOSE true\" to enable stack traces)");
         }
         else
         {
            ShellMessages.error(this, "Exception encountered: (type \"set VERBOSE false\" to disable stack traces)");
            e.printStackTrace();
         }
      }
   }

   private String formatSourcedError(final PluginMetadata plugin)
   {
      return (plugin == null ? "" : ("[" + plugin.toString() + "] "));
   }

   private String formatSourcedError(final CommandMetadata cmd)
   {
      String out;
      if (cmd != null)
      {
         out = cmd.getParent().getName();
         if (!cmd.isDefault())
            out += " " + cmd.getName();

         out = "[" + out + "] ";
      }
      else
         out = "";

      return out;
   }

   @Override
   public String readLine() throws IOException
   {
      return readLine(null);
   }

   @Override
   public String readLine(final Character mask) throws IOException
   {
      String line;
      if (mask != null)
      {
         line = reader.readLine(mask);
      }
      else
      {
         line = reader.readLine();
      }

      write((byte) '\n');
      flushBuffer();

      if (isExecuting() && (line == null))
      {
         reader.println();
         reader.flush();
         throw new AbortedException();
      }
      else if (line == null)
      {
         String eofs = (String) environment.getProperty(PROP_IGNORE_EOF);

         int propEOFs;
         try
         {
            propEOFs = Integer.parseInt(eofs);
         }
         catch (NumberFormatException e)
         {
            if (isVerbose())
               ShellMessages.info(this, "Unable to parse Shell property [" + PROP_IGNORE_EOF + "]");

            propEOFs = DEFAULT_IGNORE_EOF;
         }

         if (this.numEOF < propEOFs)
         {
            println();
            println("(Press CTRL-D again or type 'exit' to quit.)");
            this.numEOF++;
         }
         else
         {
            print("exit");
            shutdown.fire(new Shutdown());
         }
         reader.flush();
      }
      else
      {
         numEOF = 0;
      }
      return line;
   }

   @Override
   public int scan()
   {
      try
      {
         return reader.readVirtualKey();
      }
      catch (IOException e)
      {
         return -1;
      }
   }

   @Override
   public void clearLine()
   {
      print(new Ansi().eraseLine(Ansi.Erase.ALL).toString());
   }

   @Override
   public void cursorLeft(final int x)
   {
      print(new Ansi().cursorLeft(x).toString());
   }

   @Override
   public void execute(String line) throws Exception
   {
      try
      {
         executing = true;
         for (CommandInterceptor interceptor : commandInterceptors)
         {
            line = interceptor.intercept(line);
         }

         if (line != null)
            fshRuntime.run(line);
      }
      catch (Exception e)
      {
         handleException(e);
      }
      finally
      {
         executing = false;
      }
   }

   @Override
   public void execute(final File file) throws Exception
   {
      StringBuilder buf = new StringBuilder();
      InputStream instream = new BufferedInputStream(new FileInputStream(file));
      try
      {
         byte[] b = new byte[25];
         int read;

         while ((read = instream.read(b)) != -1)
         {
            for (int i = 0; i < read; i++)
            {
               buf.append((char) b[i]);
            }
         }

         instream.close();

         execute(buf.toString());
      }
      finally
      {
         instream.close();
      }
   }

   @Override
   public void execute(final File file, final String... args) throws Exception
   {
      StringBuilder buf = new StringBuilder();

      String funcName = file.getName().replaceAll("\\.", "_") + "_" + String.valueOf(hashCode()).replaceAll("\\-", "M");

      buf.append("def ").append(funcName).append('(');
      if (args != null)
      {
         for (int i = 0; i < args.length; i++)
         {
            buf.append("_").append(String.valueOf(i));
            if ((i + 1) < args.length)
            {
               buf.append(", ");
            }
         }
      }

      buf.append(") {\n");

      if (args != null)
      {
         buf.append("@_vararg = new String[").append(args.length).append("];\n");

         for (int i = 0; i < args.length; i++)
         {
            buf.append("@_vararg[").append(String.valueOf(i)).append("] = ")
                     .append("_").append(String.valueOf(i)).append(";\n");
         }
      }

      InputStream instream = new BufferedInputStream(new FileInputStream(file));
      try
      {
         byte[] b = new byte[25];
         int read;

         while ((read = instream.read(b)) != -1)
         {
            for (int i = 0; i < read; i++)
            {
               buf.append((char) b[i]);
            }
         }

         buf.append("\n}; \n@").append(funcName).append('(');

         if (args != null)
         {
            for (int i = 0; i < args.length; i++)
            {
               buf.append("\"").append(args[i].replaceAll("\\\"", "\\\\\\\"")).append("\"");
               if ((i + 1) < args.length)
               {
                  buf.append(", ");
               }
            }
         }

         buf.append(");\n");

         execute(buf.toString());
      }
      finally
      {
         environment.removeProperty(funcName);
         instream.close();
      }
   }

   /*
    * Shell Print Methods
    */
   @Override
   public void printlnVerbose(final String line)
   {
      if ((line != null) && isVerbose())
      {
         screenBuffer.write((byte) '\n');
      }
   }

   @Override
   public void print(final String output)
   {
      if (output != null)
      {
         screenBuffer.write(output);
      }
   }

   @Override
   public void println(final String line)
   {
      if (line != null)
      {
         screenBuffer.write(line);
         screenBuffer.write((byte) '\n');
      }
   }

   @Override
   public void println()
   {
      try
      {
         screenBuffer.write((byte) '\n');
         _flushBuffer();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void print(final ShellColor color, final String output)
   {
      print(renderColor(color, output));
   }

   @Override
   public void println(final ShellColor color, final String output)
   {
      println(renderColor(color, output));
   }

   @Override
   public void printlnVerbose(final ShellColor color, final String output)
   {
      printlnVerbose(renderColor(color, output));
   }

   @Override
   public String renderColor(final ShellColor color, final String output)
   {
      if (!colorEnabled)
      {
         return output;
      }

      Ansi ansi = new Ansi();

      switch (color)
      {
         case BLACK:
            ansi.fg(Ansi.Color.BLACK);
            break;
         case BLUE:
            ansi.fg(Ansi.Color.BLUE);
            break;
         case CYAN:
            ansi.fg(Ansi.Color.CYAN);
            break;
         case GREEN:
            ansi.fg(Ansi.Color.GREEN);
            break;
         case MAGENTA:
            ansi.fg(Ansi.Color.MAGENTA);
            break;
         case RED:
            ansi.fg(Ansi.Color.RED);
            break;
         case WHITE:
            ansi.fg(Ansi.Color.WHITE);
            break;
         case YELLOW:
            ansi.fg(Ansi.Color.YELLOW);
            break;
         case BOLD:
            ansi.a(Ansi.Attribute.INTENSITY_BOLD);
            break;
         case ITALIC:
            ansi.a(Ansi.Attribute.ITALIC);
            ansi.a(Ansi.Attribute.INTENSITY_FAINT);
            break;

         default:
            return output;
      }

      return ansi.render(output).reset().toString();
   }

   @Override
   public synchronized void write(final byte b)
   {
      screenBuffer.write(b);
   }

   @Override
   public void write(byte[] b)
   {
      screenBuffer.write(b);
   }

   @Override
   public void write(byte[] b, int offset, int length)
   {
      screenBuffer.write(b, offset, length);
   }


   private void _flushBuffer() throws IOException
   {
      if (bufferingMode == BufferingMode.Direct)
      {
         reader.flush();
      }
   }

   @Override
   public void clear()
   {
      print(new Ansi().cursor(0, 0).eraseScreen().toString());
   }

   @Override
   public boolean isExceptionHandlingEnabled()
   {
      Object s = environment.getProperty(PROP_EXCEPTION_HANDLING);
      return (s != null) && "true".equals(s);
   }

   @Override
   public void setExceptionHandlingEnabled(final boolean enabled)
   {
      environment.setProperty(PROP_EXCEPTION_HANDLING, String.valueOf(enabled));
   }

   @Override
   public boolean isVerbose()
   {
      Object s = environment.getProperty(PROP_VERBOSE);
      return (s != null) && "true".equals(s);
   }

   @Override
   public void setVerbose(final boolean verbose)
   {
      environment.setProperty(PROP_VERBOSE, String.valueOf(verbose));
   }

   @Override
   public boolean isPretend()
   {
      return pretend;
   }

   @Override
   public boolean isExecuting()
   {
      return executing;
   }

   @Override
   public void setInputStream(final InputStream is) throws IOException
   {
      this.inputStream = is;
      initReaderAndStreams();
   }

   @Override
   public void setOutputStream(final OutputStream stream) throws IOException
   {
      this.outputStream = stream;
      initReaderAndStreams();
   }

   @Override
   public void setDefaultPrompt()
   {
      setPrompt("");
   }

   @Override
   public void setPrompt(final String prompt)
   {
      environment.setProperty(PROP_PROMPT, prompt);
   }

   @Override
   public String getPrompt()
   {
      if (projectContext.getCurrent() != null)
      {
         return Echo.echo(this, Echo.promptExpressionParser(this, (String) environment.getProperty(PROP_PROMPT)));
      }
      else
      {
         return Echo.echo(this,
                  Echo.promptExpressionParser(this, (String) environment.getProperty(PROP_PROMPT_NO_PROJ)));
      }
   }

   @Override
   public DirectoryResource getCurrentDirectory()
   {
      Resource<?> r = getCurrentResource();
      return ResourceUtil.getContextDirectory(r);
   }

   @Override
   public DirectoryResource getConfigDir()
   {
      return resourceFactory.getResourceFrom(new File((String) environment.getProperty(PROP_FORGE_CONFIG_DIR))).reify(
               DirectoryResource.class);
   }

   @Override
   public Resource<?> getCurrentResource()
   {
      Resource<?> result = this.projectContext.getCurrentResource();
      if (result == null)
      {
         result = this.resourceFactory.getResourceFrom(Files.getWorkingDirectory());
         environment.setProperty("CWD", result.getFullyQualifiedName());
      }

      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Class<? extends Resource<?>> getCurrentResourceScope()
   {
      return (Class<? extends Resource<?>>) getCurrentResource().getClass();
   }

   @Override
   public void setCurrentResource(final Resource<?> resource)
   {
      lastResource = getCurrentResource();
      projectContext.setCurrentResource(resource);
      environment.setProperty("CWD", resource.getFullyQualifiedName());
   }

   @Override
   public Project getCurrentProject()
   {
      return this.projectContext.getCurrent();
   }

   @Override
   public int getHeight()
   {
      return screenBuffer.getHeight();
   }

   @Override
   public int getAbsoluteHeight()
   {
      return reader.getTerminal().getHeight();
   }

   @Override
   public int getWidth()
   {
      return screenBuffer.getWidth();
   }

   public String escapeCode(final int code, final String value)
   {
      return new Ansi().a(value).fg(Ansi.Color.BLUE).toString();
   }

   @Override
   public String promptWithCompleter(String message, final Completer tempCompleter)
   {
      if (!message.isEmpty() && message.matches("^.*\\S$"))
      {
         message = message + " ";
      }
      message = renderColor(ShellColor.CYAN, " ? ") + message;

      try
      {
         reader.removeCompleter(this.completer);
         if (tempCompleter != null)
         {
            reader.addCompleter(tempCompleter);
         }
         reader.setHistoryEnabled(false);
         reader.setPrompt(message);
         flushBuffer();
         String read = readLine();
         flushBuffer();
         return read;
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Shell input stream failure", e);
      }
      finally
      {
         if (tempCompleter != null)
         {
            reader.removeCompleter(tempCompleter);
         }
         reader.addCompleter(this.completer);
         reader.setHistoryEnabled(true);
         reader.setPrompt("");
      }
   }

   @Override
   public String promptSecret(String message)
   {
      if (!message.isEmpty() && message.matches("^.*\\S$"))
      {
         message = message + " ";
      }
      message = renderColor(ShellColor.CYAN, " ? ") + message;

      try
      {
         reader.removeCompleter(this.completer);
         reader.setHistoryEnabled(false);
         reader.setPrompt(message);
         flushBuffer();
         String line = readLine('*');
         flushBuffer();
         return line;
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Shell input stream failure", e);
      }
      finally
      {
         reader.addCompleter(this.completer);
         reader.setHistoryEnabled(true);
         reader.setPrompt("");
      }
   }

   @Override
   protected PromptTypeConverter getPromptTypeConverter()
   {
      return promptTypeConverter;
   }

   @Override
   protected ResourceFactory getResourceFactory()
   {
      return resourceFactory;
   }

   @Override
   public void setAnsiSupported(final boolean value)
   {
      if (value != isAnsiSupported())
      {
         try
         {
            if (value)
            {
               configureOSTerminal();
            }
            else
            {
               TerminalFactory.configure(TerminalFactory.Type.NONE);
               TerminalFactory.reset();
            }
            initReaderAndStreams();
         }
         catch (IOException e)
         {
            throw new RuntimeException("Failed to reset Terminal instance for ANSI configuration", e);
         }
      }
   }

   @Override
   public void bufferingMode()
   {
      screenBuffer.bufferOnlyMode();
   }

   @Override
   public void directWriteMode()
   {
      screenBuffer.directWriteMode();
   }

   @Override
   public void flushBuffer()
   {
      screenBuffer.flushBuffer();
   }

   @Override
   public void registerBufferManager(BufferManager manager)
   {
      screenBuffer = manager;
   }

   public BufferManager getBufferManager()
   {
      return screenBuffer;
   }

//   private void initBuffer()
//   {
//      screenBuffer = new JLineScreenBuffer(reader.getTerminal(), );
//   }

   private void configureOSTerminal() throws IOException
   {
      if (OSUtils.isLinux() || OSUtils.isOSX())
      {
         TerminalFactory.configure(TerminalFactory.Type.UNIX);
         TerminalFactory.reset();
      }
      else if (OSUtils.isWindows())
      {
         TerminalFactory.configure(TerminalFactory.Type.WINDOWS);
         TerminalFactory.reset();
      }
      else
      {
         TerminalFactory.configure(TerminalFactory.Type.NONE);
         TerminalFactory.reset();
      }
      initReaderAndStreams();
   }

   @Override
   public boolean isAnsiSupported()
   {
      return reader.getTerminal().isAnsiSupported();
   }

   @Override
   public ForgeEnvironment getEnvironment()
   {
      return environment;
   }

   @Override
   BeanManager getBeanManager()
   {
      return manager;
   }

   public ConsoleReader getReader()
   {
      return reader;
   }
}