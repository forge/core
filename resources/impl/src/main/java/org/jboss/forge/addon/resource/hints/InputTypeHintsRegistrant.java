/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.hints;

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.hints.HintsLookup;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.furnace.container.simple.EventListener;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.event.PostStartup;

/**
 * Only active when ui-spi addon is installed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class InputTypeHintsRegistrant implements EventListener
{
   private static final Logger log = Logger.getLogger(InputTypeHintsRegistrant.class.getName());

   @Override
   public void handleEvent(Object event, Annotation... qualifiers)
   {
      if (event instanceof PostStartup)
      {
         try
         {
            Environment environment = SimpleContainer.getServices(getClass().getClassLoader(), Environment.class).get();
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
}
