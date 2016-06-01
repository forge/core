/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction.file;

import java.lang.annotation.Annotation;

import org.jboss.forge.furnace.container.simple.EventListener;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.event.PreShutdown;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class FileResourceEventListener implements EventListener
{
   @Override
   public void handleEvent(Object event, Annotation... qualifiers)
   {
      if (event instanceof PreShutdown)
      {
         PreShutdown preShutdown = (PreShutdown) event;
         if (SimpleContainer.getAddon(getClass().getClassLoader()).equals(preShutdown.getAddon()))
         {
            FileResourceTransactionManager transactionManager = SimpleContainer
                     .getServices(getClass().getClassLoader(), FileResourceTransactionManager.class).get();
            transactionManager.shutdown(preShutdown);
         }
      }
   }
}
