/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.console.helper.ManProvider;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.util.Streams;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ForgeManProvider implements ManProvider
{
   private static final Logger log = Logger.getLogger(ForgeManProvider.class.getName());

   private final ShellImpl shell;
   private final CommandManager manager;

   private final Comparator<? super InputComponent<?, ?>> shortNameComparator = new Comparator<InputComponent<?, ?>>()
   {
      @Override
      public int compare(InputComponent<?, ?> left, InputComponent<?, ?> right)
      {
         return new Character(left.getShortName()).compareTo(right.getShortName());
      }
   };

   private final Comparator<? super InputComponent<?, ?>> nameComparator = new Comparator<InputComponent<?, ?>>()
   {
      @Override
      public int compare(InputComponent<?, ?> left, InputComponent<?, ?> right)
      {
         return left.getName().compareTo(right.getName());
      }
   };

   public ForgeManProvider(ShellImpl shell, CommandManager manager)
   {
      this.shell = shell;
      this.manager = manager;
   }

   @Override
   public InputStream getManualDocument(String command)
   {
      try (ShellContextImpl context = shell.createUIContext())
      {
         for (UICommand cmd : manager.getAllCommands())
         {
            if (command.equals(manager.getCommandName(context, cmd)))
            {
               URL docLocation = cmd.getMetadata(context).getDocLocation();
               if (docLocation != null)
               {
                  try
                  {
                     return docLocation.openStream();
                  }
                  catch (IOException e)
                  {
                     log.log(Level.SEVERE,
                              "Could not open man page document stream URL [" + docLocation.toExternalForm()
                                       + "] for command [" + cmd.getMetadata(context).getType().getName() + "].", e);
                  }
               }

               return buildDefaultManPage(cmd, context);
            }
         }
      }
      return null;
   }

   private InputStream buildDefaultManPage(UICommand cmd, final UIContext context)
   {
      try
      {
         URL template = getClass().getResource("DefaultManPage.txt");
         String result = Streams.toString(template.openStream());

         final List<InputComponent<?, ?>> inputs = new ArrayList<>();
         cmd.initializeUI(new UIBuilder()
         {

            @Override
            public UIContext getUIContext()
            {
               return context;
            }

            @Override
            public UIBuilder add(InputComponent<?, ?> input)
            {
               inputs.add(input);
               return this;
            }
         });

         result = result.replaceAll("%name%", manager.getCommandName(context, cmd));
         result = result.replaceAll("%description%", cmd.getMetadata(context).getCategory().toString());

         result = result.replaceAll("%synopsis%", buildSynopsis(cmd, context, inputs));
         result = result.replaceAll("%options%", buildOptions(cmd, context, inputs));
         result = result.replaceAll("%addon%", getSourceAddonName(cmd, context));

         return new ByteArrayInputStream(result.getBytes());
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error opening stream to default man page.", e);
      }
   }

   private String getSourceAddonName(UICommand cmd, final UIContext context)
   {
      return cmd.getMetadata(context).getType().getClassLoader().toString();
   }

   private String buildSynopsis(UICommand cmd, UIContext context, List<InputComponent<?, ?>> inputs)
   {
      StringBuilder result = new StringBuilder();
      result.append(manager.getCommandName(context, cmd)).append(" [-");

      Collections.sort(inputs, shortNameComparator);
      for (InputComponent<?, ?> input : inputs)
      {
         if (input.getShortName() != InputComponents.DEFAULT_SHORT_NAME && Boolean.class.equals(input.getValueType()))
         {
            result.append(input.getShortName());
         }
      }

      result.append("] ");

      Collections.sort(inputs, nameComparator);
      InputComponent<?, ?> arguments = null;
      for (InputComponent<?, ?> input : inputs)
      {
         if ("arguments".equals(input.getName()))
         {
            arguments = input;
         }
         else if (input.isRequired())
         {
            result.append("[");
            if (input.getShortName() != InputComponents.DEFAULT_SHORT_NAME)
            {
               result.append("-").append(input.getShortName()).append(" ");
            }
            result.append("--").append(input.getName()).append("] ");
            result.append(input.getValueType().getSimpleName()).append(" ");
         }
      }

      if (arguments != null)
      {
         result.append("[ ... ").append(arguments.getLabel() == null ? " args" : arguments.getLabel()).append("] ");
         result.append(arguments.getValueType().getSimpleName()).append(" ");
      }

      return result.toString();
   }

   private String buildOptions(UICommand cmd, UIContext context, List<InputComponent<?, ?>> inputs)
   {
      StringBuilder result = new StringBuilder();

      if (cmd.getMetadata(context).getDescription() != null)
         result.append(cmd.getMetadata(context).getDescription());
      if (UIWizard.class.isAssignableFrom(cmd.getMetadata(context).getType()))
         result.append(" (*multi-step wizard* - some options may not be auto-documented in this man page.)");

      result.append("\n\n");

      Collections.sort(inputs, shortNameComparator);
      InputComponent<?, ?> arguments = null;
      for (InputComponent<?, ?> input : inputs)
      {
         if ("arguments".equals(input.getName()))
         {
            arguments = input;
         }
         else
         {
            result.append("   ");
            if (input.getShortName() != InputComponents.DEFAULT_SHORT_NAME)
               result.append("-").append(input.getShortName()).append("   ");

            result.append(input.getName());
            result.append("\n      ");

            if (!input.getName().equals(input.getLabel()))
            {
               result.append(input.getLabel() == null ? "" : input.getLabel());

               if (input.getLabel() != null && input.getDescription() != null)
                  result.append(" - ");
            }

            result.append(input.getDescription() == null ? "" : input.getDescription());
            result.append(" [").append(input.getValueType().getSimpleName()).append("]");
            if (input.isRequired())
               result.append(" (*required*)");

            result.append("\n\n");
         }
      }

      if (arguments != null)
      {
         result.append("[");
         result.append(arguments.getLabel() == null ? "L " + arguments.getValueType().getSimpleName()
                  + "; ... arguments"
                  : arguments.getLabel());
         result.append("] ");
      }

      return result.toString();
   }
}
