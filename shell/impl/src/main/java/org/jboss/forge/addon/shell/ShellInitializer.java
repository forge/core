package org.jboss.forge.addon.shell;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Starts up the shell if not in the IDE
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ShellInitializer
{
   private Shell shell;

   @Inject
   private ShellFactory shellFactory;

   public void startupDefaultShell(@Observes PostStartup startup) throws Exception
   {
      if (!Boolean.getBoolean("forge.compatibility.IDE"))
      {
         Settings settings = new SettingsBuilder().create();
         this.shell = shellFactory.createShell(OperatingSystemUtils.getUserHomeDir(), settings);
      }
   }

   public void shutdown(@Observes PreShutdown preShutdown)
   {
      destroyShell();
   }

   @PreDestroy
   public void destroyShell()
   {
      if (this.shell != null)
      {
         this.shell.close();
         this.shell = null;
      }
   }
}
