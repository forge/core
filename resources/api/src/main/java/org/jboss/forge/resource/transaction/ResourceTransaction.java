/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.transaction;

import org.jboss.forge.resource.ResourceException;

/**
 * The ResourceTransaction interface allows operations to be performed on transactions
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface ResourceTransaction
{
   /**
    * Attempt to commit this transaction, discarding the current {@link ChangeSet}.
    *
    * @throws ResourceException if any operation fails
    */
   void commit() throws ResourceException;

   /**
    * Rolls back this transaction. This transaction should not be used again
    *
    * This also discards the current {@link ChangeSet}
    *
    * @throws ResourceException
    */
   void rollback() throws ResourceException;

   /**
    * @return A {@link ChangeSet} object containing which resources were modified during this transaction
    */
   ChangeSet getChangeSet();

}
