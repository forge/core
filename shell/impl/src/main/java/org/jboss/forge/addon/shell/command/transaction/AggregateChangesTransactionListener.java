/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;

public class AggregateChangesTransactionListener implements ResourceTransactionListener
{
   private final Set<ResourceEvent> events = new HashSet<>();

   @Override
   public void transactionCommitted(ResourceTransaction transaction, Set<ResourceEvent> changeSet)
   {
      this.events.addAll(changeSet);
   }

   @Override
   public void transactionStarted(ResourceTransaction transaction)
   {
   }

   @Override
   public void transactionRolledBack(ResourceTransaction transaction)
   {
   }

   public Set<ResourceEvent> getResourceEvents()
   {
      return events;
   }

   public void clear()
   {
      events.clear();
   }
}