/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.transaction;

import java.util.Set;

import org.jboss.forge.addon.resource.Resource;

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
}
