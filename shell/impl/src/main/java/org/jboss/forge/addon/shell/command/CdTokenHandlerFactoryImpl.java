/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.shell.spi.command.CdTokenHandler;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandlerFactory;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;

@Singleton
public class CdTokenHandlerFactoryImpl implements CdTokenHandlerFactory
{
   @Inject
   private Imported<CdTokenHandler> importedHandlers;

   private List<CdTokenHandler> manuallyRegisteredHandlers = new ArrayList<>();

   public void shutdown(@Observes @Local PreShutdown shutdown)
   {
      manuallyRegisteredHandlers.clear();
   }

   @Override
   public List<CdTokenHandler> getHandlers()
   {
      List<CdTokenHandler> result = new ArrayList<>();
      for (CdTokenHandler cdTokenHandler : importedHandlers)
      {
         result.add(cdTokenHandler);
      }

      result.addAll(manuallyRegisteredHandlers);

      return result;
   }

   @Override
   public ListenerRegistration<CdTokenHandler> addTokenHandler(final CdTokenHandler handler)
   {
      manuallyRegisteredHandlers.add(handler);
      return new ListenerRegistration<CdTokenHandler>()
      {
         @Override
         public CdTokenHandler removeListener()
         {
            manuallyRegisteredHandlers.remove(handler);
            return handler;
         }
      };
   }

}
