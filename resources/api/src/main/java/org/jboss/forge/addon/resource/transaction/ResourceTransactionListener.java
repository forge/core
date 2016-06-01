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
 * Listener for observing events of the {@link ResourceTransaction} lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResourceTransactionListener
{
   /**
    * Triggered {@link ResourceTransaction#begin()} is called successfully.
    */
   void transactionStarted(ResourceTransaction transaction);

   /**
    * Triggered {@link ResourceTransaction#commit()} is called successfully, passing the {@link Set} of {@link Resource}
    * instances changed during the transaction.
    */
   void transactionCommitted(ResourceTransaction transaction, Set<ResourceEvent> changeSet);

   /**
    * Triggered {@link ResourceTransaction#rollback()} is called successfully.
    */
   void transactionRolledBack(ResourceTransaction transaction);
}
