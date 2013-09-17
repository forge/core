package org.jboss.forge.addon.shell;

import java.io.File;

import org.jboss.aesh.console.settings.Settings;

/**
 * Creates {@link Shell} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ShellFactory
{
   /**
    * Create a {@link Shell} based on the specified {@link Settings}
    */
   public Shell createShell(File initialSelection, Settings settings);

}
