/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import java.util.Set;

import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class OtherTransactionStartedListener implements ResourceTransactionListener
{
   private boolean inForeignTransaction;
   private boolean enabled = true;

   @Override
   public void transactionStarted(ResourceTransaction transaction)
   {
      if (enabled)
         setInForeignTransaction(true);
   }

   @Override
   public void transactionCommitted(ResourceTransaction transaction, Set<ResourceEvent> changeSet)
   {
      if (enabled)
         setInForeignTransaction(false);
   }

   @Override
   public void transactionRolledBack(ResourceTransaction transaction)
   {
      if (enabled)
         setInForeignTransaction(false);
   }

   public void setInForeignTransaction(boolean inTransaction)
   {
      this.inForeignTransaction = inTransaction;
   }

   public boolean isInForeignTransaction()
   {
      return inForeignTransaction;
   }

   public void disable()
   {
      enabled = false;
   }

   public void enable()
   {
      enabled = true;
   }

}
