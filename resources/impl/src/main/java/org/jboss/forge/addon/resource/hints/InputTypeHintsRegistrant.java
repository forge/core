package org.jboss.forge.addon.resource.hints;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.hints.HintsLookup;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.furnace.event.PostStartup;

/**
 * Only active when ui-spi addon is installed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class InputTypeHintsRegistrant
{
   private static final Logger log = Logger.getLogger(InputTypeHintsRegistrant.class.getName());

   private Environment environment;

   @Inject
   public InputTypeHintsRegistrant(Environment environment)
   {
      this.environment = environment;
   }

   public void initialize(@Observes PostStartup event)
   {
      try
      {
         HintsLookup hints = new HintsLookup(environment);
         hints.setInputType(FileResource.class, InputType.FILE_PICKER);
         hints.setInputType(DirectoryResource.class, InputType.DIRECTORY_PICKER);
      }
      catch (Throwable e)
      {
         log.log(Level.FINE,
                  "Could not register resources InputType hints. Resources addon is probably not loaded yet.", e);
      }
   }
}
