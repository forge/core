package org.jboss.forge.resource.hints;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.environment.Environment;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.ui.hints.HintsLookup;
import org.jboss.forge.ui.hints.InputTypes;

/**
 * Only active when ui-hints addon is installed.
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
      hints.setInputType(FileResource.class, InputTypes.FILE_PICKER);
      hints.setInputType(DirectoryResource.class, InputTypes.FILE_PICKER);
      System.out.println("Initialized Resources InputType Hints");
   }
}
