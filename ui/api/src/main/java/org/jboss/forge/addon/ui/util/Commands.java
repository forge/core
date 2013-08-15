package org.jboss.forge.addon.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A utiliy class to handle commands
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Commands
{
   /**
    * Returns the main commands from this {@link Iterable} (that is, the ones that are enabled and not a
    * {@link UIWizardStep} instance)
    */
   public static Iterable<UICommand> getEnabledCommands(Iterable<UICommand> commands, UIContext context)
   {
      List<UICommand> result = new ArrayList<UICommand>();
      for (UICommand uiCommand : commands)
      {
         if (uiCommand.isEnabled(context) && !(uiCommand instanceof UIWizardStep))
         {
            result.add(uiCommand);
         }
      }
      return result;
   }
}
