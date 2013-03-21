package org.jboss.forge.parser.java.hints;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.container.event.Startup;
import org.jboss.forge.environment.Environment;
import org.jboss.forge.parser.java.resources.JavaResource;
import org.jboss.forge.ui.hints.HintsLookup;
import org.jboss.forge.ui.hints.InputTypes;

/**
 * Only active when ui-hints addon is installed.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
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

   public void initialize(@Observes Startup event)
   {
      HintsLookup hints = new HintsLookup(environment);
      hints.setInputType(JavaResource.class, InputTypes.JAVA_CLASS_PICKER);
   }
}