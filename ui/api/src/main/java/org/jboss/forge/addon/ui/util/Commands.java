package org.jboss.forge.addon.ui.util;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
}
