package org.jboss.forge.addon.shell.ui;

import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.line.CommandLine;
import org.jboss.forge.addon.ui.command.UICommand;
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
   Shell getProvider();

   /**
    * @return <code>true</code> if it's in interactive mode
    */
   boolean isInteractive();

   /**
    * @return <code>true</code> if should display errors
    */
   boolean isVerbose();

   /**
    * @return the {@link CommandLine} used in this command execution
    */
   CommandLine getCommandLine();
}
