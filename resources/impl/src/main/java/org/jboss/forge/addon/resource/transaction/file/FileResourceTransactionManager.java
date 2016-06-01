/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction.file;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FileResourceTransactionManager
{
   private final Logger logger = Logger.getLogger(getClass().getName());

   private FileResourceTransactionImpl transaction;

   private final List<ResourceTransactionListener> listeners = new CopyOnWriteArrayList<>();

   public void shutdown(PreShutdown shutdown)
   {
      if (transaction != null)
         try
         {
            transaction.close();
         }
         catch (IOException e)
         {
            logger.log(Level.SEVERE, "Error while shutting down XAFileSystem", e);
         }
   }

   public FileResourceTransactionImpl getCurrentTransaction(ResourceFactory resourceFactory)
   {
      if (transaction == null)
      {
         transaction = new FileResourceTransactionImpl(this, resourceFactory);
      }
      return transaction;
   }

   public ListenerRegistration<ResourceTransactionListener> addTransactionListener(
            final ResourceTransactionListener listener)
   {
      listeners.add(listener);
      return new ListenerRegistration<ResourceTransactionListener>()
      {
         @Override
         public ResourceTransactionListener removeListener()
         {
            listeners.remove(listener);
            return listener;
         }
      };
   }

   public List<ResourceTransactionListener> getTransactionListeners()
   {
      return listeners;
   }

}
