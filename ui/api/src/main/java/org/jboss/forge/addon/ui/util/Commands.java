/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A utiliy class to handle commands
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Commands
{
   private static final Pattern WHITESPACES = Pattern.compile("\\W+");
   private static final Pattern COLONS = Pattern.compile("\\:");
   private static final Logger log = Logger.getLogger(Commands.class.getName());

   /**
    * Returns the main commands from this {@link Iterable} (that is, the ones that are enabled and not a
    * {@link UIWizardStep} instance)
    */
   public static Iterable<UICommand> getEnabledCommands(Iterable<UICommand> commands, UIContext context)
   {
      List<UICommand> result = new LinkedList<>();
      for (UICommand uiCommand : commands)
      {
         try
         {
            if (isEnabled(uiCommand, context))
            {
               result.add(uiCommand);
            }
         }
         catch (Exception e)
         {
            log.log(Level.SEVERE, "Could not call method " + UICommand.class.getName()
                     + "`isEnabled(UIContext ctx)` of type [" + uiCommand + "] with Metadata ["
                     + getMetadata(uiCommand, context) + "].", e);
         }
      }
      return result;
   }

   /**
    * Returns true if this command can be invoked
    */
   public static boolean isEnabled(UICommand command, UIContext context)
   {
      return (command.isEnabled(context) && !(command instanceof UIWizardStep));
   }

   private static String getMetadata(UICommand command, UIContext context)
   {
      String result = "!!! Failed to load Metadata via `" + UICommand.class.getName()
               + ".getMetadata(UIContext ctx)` !!!";
      try
      {
         UICommandMetadata metadata = command.getMetadata(context);
         result = metadata.toString();
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, "Could not call method " + UICommand.class.getName()
                  + "`getMetadata(UIContext ctx)` of type [" + command + "].", e);
      }
      return result;
   }

   /**
    * "Shellifies" a name (that is, makes the name shell-friendly) by replacing spaces with "-" and removing colons
    *
    * @param name
    * @return
    */
   private static String shellifyName(String name)
   {
      String newName = WHITESPACES.matcher(name.trim()).replaceAll("-");
      // /FORGE-2690: Special case when command name ends with -
      if (newName.endsWith("-"))
      {
         newName = newName.substring(0, newName.length() - 1);
      }
      return COLONS.matcher(newName).replaceAll("");
   }

   /**
    * Shellifies a command name
    * 
    * @param name
    * @return
    */
   public static String shellifyCommandName(String name)
   {
      return shellifyName(name).toLowerCase();
   }

   /**
    * Shellifies an option name
    * 
    * @param name
    * @return
    */
   public static String shellifyOptionName(String name)
   {
      return shellifyName(name);
   }

   /**
    * Shellifies an option value
    * 
    * @param value
    * @return
    */
   public static String shellifyOptionValue(String value)
   {
      return COLONS.matcher(WHITESPACES.matcher(value.trim()).replaceAll("_")).replaceAll("").toUpperCase();
   }

   /**
    * Shellifies an option name using the provided style
    * 
    * @param name
    * @param style
    * @return
    */
   public static String shellifyOptionNameDashed(String name)
   {
      String shellName = shellifyName(name);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < shellName.length(); i++)
      {
         char c = shellName.charAt(i);
         if (Character.isUpperCase(c))
         {
            if (i > 0)
            {
               char previousChar = shellName.charAt(i - 1);
               char nextChar = (i + 1 < shellName.length()) ? shellName.charAt(i + 1) : '\0';
               if (previousChar != '-' && (!Character.isUpperCase(previousChar) || Character.isLowerCase(nextChar)))
               {
                  sb.append('-');
               }
            }
            sb.append(Character.toLowerCase(c));
         }
         else
         {
            sb.append(c);
         }
      }
      return sb.toString();
   }
}