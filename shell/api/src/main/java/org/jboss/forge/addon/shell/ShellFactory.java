package org.jboss.forge.addon.shell;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.furnace.services.Exported;

/**
 * Creates {@link Shell} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Exported
public interface ShellFactory
{
   /**
    * Create a {@link Shell} based on the specified {@link Settings}
    */
   public Shell createShell(Settings settings);
}
