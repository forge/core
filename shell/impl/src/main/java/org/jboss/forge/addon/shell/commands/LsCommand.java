package org.jboss.forge.addon.shell.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.aesh.util.Parser;
import org.jboss.forge.addon.shell.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

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
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
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
      return Results.success(listMany(arguments.getValue(), (ShellContext) context));
   }

   private String listMany(Iterable<File> files, ShellContext context)
   {
      if (arguments.getValue() != null)
      {
         StringBuilder builder = new StringBuilder();
          for (File file : arguments.getValue()) {
              if (builder.length() > 0) {
                  builder.append("\n").append(file.getAbsolutePath()).append("\n");
              }
              builder.append(listLs(file, context));
          }
          return builder.toString();
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
