package org.jboss.forge.aesh.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.aesh.util.Parser;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UICompleter;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UIInputComponent;
import org.jboss.forge.ui.input.UIInputMany;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class LsCommand implements UICommand
{
   @Inject
   private UIInput<String> about;

   @Inject
   private UIInputMany<File> arguments;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("ls").description("List files");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return context instanceof ShellContext;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      about.setLabel("about");
      about.setRequired(false);
      about.setDefaultValue("");

      about.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIInputComponent<?, String> input, String value)
         {
            List<String> out = new ArrayList<String>();
            out.add("foo1");
            return out;
         }
      });
      builder.add(about);

      arguments.setLabel("");
      arguments.setRequired(false);

      if (builder.getUIContext() instanceof ShellContext)
      {
         Object selection = builder.getUIContext().getInitialSelection().get();
         if (selection instanceof File)
            arguments.setDefaultValue(Arrays.asList((File) selection));
      }

      /*
       * not needed for File arguments.setCompleter(new UICompleter<String>() {
       * 
       * @Override public Iterable<String> getCompletionProposals(UIInputComponent<?,String> input, String value) {
       * List<String> out = new ArrayList<String>(); out.add("arguments!"); return out; } });
       */

      builder.add(arguments);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      StringBuilder builder = new StringBuilder();
      if (arguments.getValue() != null)
      {
         Iterator<File> iter = arguments.getValue().iterator();
         while (iter.hasNext())
         {
            builder.append(iter.next().getAbsolutePath() + ", ");
         }
      }

      return Results.success(listMany(arguments.getValue(), (ShellContext) context));
   }

   private String listMany(Iterable<File> files, ShellContext context)
   {
      if (arguments.getValue() != null)
      {
         Iterator<File> iter = arguments.getValue().iterator();
         while (iter.hasNext())
         {
            return listLs(iter.next(), context);
         }
      }
      return null;
   }

   private String listLs(File path, ShellContext context)
   {
      if (path.isDirectory())
      {
         List<String> files = new ArrayList<String>();
         for (File f : path.listFiles())
            files.add(f.getName());
         return Parser.formatDisplayList(files,
                  context.getShell().getConsole().getTerminalSize().getHeight(),
                  context.getShell().getConsole().getTerminalSize().getWidth());
      }
      else if (path.isFile())
         return path.getName();
      else
         return null;
   }
}
