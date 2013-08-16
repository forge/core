package org.jboss.forge.addon.shell;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.furnace.event.PostStartup;

/**
 * Starts up the shell if not in the IDE
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellInitializer
{
   @Inject
   private ShellFactory shellFactory;

   public void startupDefaultShell(@Observes PostStartup startup) throws Exception
   {
      if (!Boolean.getBoolean("forge.compatibility.IDE"))
      {
         Settings settings = new SettingsBuilder().create();
         shellFactory.createShell(settings);
      }
   }
}
