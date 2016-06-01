/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.hints;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.ui.hints.HintsLookup;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.furnace.event.PostStartup;

/**
 * Only active when ui-spi addon is installed.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
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
         hints.setInputType(JavaResource.class, InputType.JAVA_CLASS_PICKER);
      }
      catch (Throwable e)
      {
         log.log(Level.FINE,
                  "Could not register parser-java InputType hints. Resources addon is probably not loaded yet.", e);
      }
   }
}