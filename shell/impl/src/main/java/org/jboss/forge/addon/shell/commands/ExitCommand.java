package org.jboss.forge.addon.shell.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ExitCommand extends AbstractShellCommand implements UIContextListener
{
   private static final String FORGE_SHUTDOWN_ATTRIBUTE = "forge.shutdown";

   @Inject
   private Furnace forge;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("exit").description("Exit the shell");
   }

   @Override
   public void initializeUI(UIBuilder context) throws Exception
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      uiContext.setAttribute(FORGE_SHUTDOWN_ATTRIBUTE, Boolean.TRUE);
      return Results.success();
   }

   @Override
   public void contextDestroyed(UIContext context)
   {
      Boolean shutdown = (Boolean) context.getAttribute(FORGE_SHUTDOWN_ATTRIBUTE);
      if (shutdown != null && shutdown)
      {
         Shell shell = (Shell) context.getProvider();
         shell.getConsole().stop();
         forge.stop();
      }
   }

   @Override
   public void contextInitialized(UIContext context)
   {
      // Do nothing
   }

}
