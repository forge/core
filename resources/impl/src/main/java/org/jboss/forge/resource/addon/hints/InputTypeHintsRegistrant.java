package org.jboss.forge.resource.addon.hints;

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
   private Environment environment;

   @Inject
   public InputTypeHintsRegistrant(Environment environment)
   {
      this.environment = environment;
   }

   public void initialize(@Observes PostStartup event)
   {
      HintsLookup hints = new HintsLookup(environment);
      hints.setInputType(FileResource.class, InputType.FILE_PICKER);
      hints.setInputType(DirectoryResource.class, InputType.FILE_PICKER);
   }
}
