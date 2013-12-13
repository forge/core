package org.jboss.forge.addon.shell;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.furnace.container.cdi.events.Local;
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

   public void startupDefaultShell(@Observes @Local PostStartup startup) throws Exception
   {
      if (Boolean.getBoolean("forge.standalone"))
      {
         Settings settings = new SettingsBuilder().create();
         this.shell = shellFactory.createShell(OperatingSystemUtils.getWorkingDir(), settings);
      }
   }

   public void shutdown(@Observes @Local PreShutdown preShutdown)
   {
      destroyShell();
   }

   @PreDestroy
   public void destroyShell()
   {
      if (this.shell != null)
      {
         try
         {
            this.shell.close();
         }
         catch (Exception ignore)
         {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Error while closing Shell", ignore);
         }
         this.shell = null;
      }
   }
}
