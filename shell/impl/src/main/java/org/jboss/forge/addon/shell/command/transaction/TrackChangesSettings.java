/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class TrackChangesSettings
{
   private boolean trackChanges;

   @Inject
   private ResourceFactory factory;

   private final List<ListenerRegistration<?>> registrations = new ArrayList<>();
   private final OtherTransactionStartedListener otherTransactionListener = new OtherTransactionStartedListener();
   private ListenerRegistration<ResourceTransactionListener> otherTransactionListenerRegistration;

   protected void init(@Observes @Local PostStartup event)
   {
      otherTransactionListenerRegistration = factory.addTransactionListener(otherTransactionListener);
   }

   protected void init(@Observes @Local PreShutdown event)
   {
      if (otherTransactionListenerRegistration != null)
         otherTransactionListenerRegistration.removeListener();

      for (ListenerRegistration<?> registration : registrations)
      {
         registration.removeListener();
      }
   }

   public boolean isTrackChanges()
   {
      return trackChanges;
   }

   public void setTrackChanges(Shell shell, boolean trackChanges)
   {
      this.trackChanges = trackChanges;

      if (trackChanges)
      {
         AggregateChangesTransactionListener aggregateChangeTxListener = new AggregateChangesTransactionListener();
         AutomaticTransactionCommandListener commandListener = new AutomaticTransactionCommandListener(
                  factory,
                  otherTransactionListener,
                  aggregateChangeTxListener);
         registrations.add(shell.addCommandExecutionListener(commandListener));
         registrations.add(factory.addTransactionListener(aggregateChangeTxListener));
      }
      else
      {
         for (ListenerRegistration<?> registration : registrations)
         {
            registration.removeListener();
         }
         registrations.clear();
      }
   }

   public boolean isInForeignTransaction()
   {
      return otherTransactionListener.isInForeignTransaction();
   }
}
