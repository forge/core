package org.jboss.forge.addon.shell.ui;

import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A {@link ShellContext} is passed to {@link UICommand} objects when a command is invoked through a shell. This object
 * is not meant to be shared across different {@link UICommand} implementations (except when it is used in a
 * {@link UIWizard} and {@link UIWizardStep})
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ShellContext extends UIContext
{
   @Override
   public Shell getProvider();

   /**
    * Returns the {@link ConsoleOutput} from this operation
    */
   public ConsoleOutput getConsoleOutput();
}
