/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction;

import java.util.Set;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.events.ResourceEvent;

/**
 * A {@link ResourceTransaction} manages changes to a group of {@link Resource}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ResourceTransaction
{
   /**
    * Starts a transaction. Throws {@link ResourceTransactionException} if the transaction is already started
    */
   public void begin() throws ResourceTransactionException;

   /**
    * Applies every change in the {@link Set} returned by {@link ResourceTransaction#getChangeSet()}
    */
   public void commit() throws ResourceTransactionException;

   /**
    * Discards this transaction and the associated changes with it
    */
   public void rollback() throws ResourceTransactionException;

   /**
    * Returns true if this transaction is started and was not already committed/rolled back
    */
   public boolean isStarted();

   /**
    * Set the the duration in seconds after which this {@link ResourceTransaction} will time out and be automatically
    * rolled back. (Timeout starts when {@link #begin()} is called.)
    * 
    * If an application has not called this method, the transaction service uses a default value for the transaction
    * timeout.
    * 
    * @param seconds The value of the timeout in seconds. If the value is zero, the transaction service restores the
    *           default value. If the value is negative a {@link ResourceTransactionException} is thrown.
    */
   public void setTransactionTimeout(int seconds);

   /**
    * Get the the duration in seconds after which this {@link ResourceTransaction} will time out and be automatically
    * rolled back. If the value returned is zero, the system is using a default timeout value. (Timeout starts when
    * {@link #begin()} is called.)
    */
   public int getTransactionTimeout();

   /**
    * The changes associated with this transaction.
    * 
    * @return an immutable {@link Set} with the changes that were introduced so far
    */
   public Set<ResourceEvent> getChangeSet();
}
