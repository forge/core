package org.jboss.forge.addon.shell;

import java.io.File;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.addon.resource.Resource;

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
   Shell createShell(File initialSelection, Settings settings);

   /**
    * Create a {@link Shell} based on the specified {@link Settings}
    */
   Shell createShell(Resource<?> intialSelection, Settings settings);
}
