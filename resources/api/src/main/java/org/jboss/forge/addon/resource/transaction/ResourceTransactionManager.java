/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.transaction;

import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.furnace.services.Exported;

/**
 * The ResourceTransactionManager interface allows
 *
 * <ul>
 * <li>Starting a {@link ResourceTransaction}</li>
 * <li>Getting the current {@link ResourceTransaction}</li>
 * </ul>
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public interface ResourceTransactionManager
{
   /**
    * Start a new transaction.
    *
    * If a transaction was already started, a {@link ResourceException} will be thrown
    *
    * @return a new {@link ResourceTransaction} instance
    * @throws ResourceException if a transaction was already started
    */
   ResourceTransaction startTransaction() throws ResourceException;

   /**
    * @return The current transaction or null if {@link ResourceTransactionManager#startTransaction()} was not
    *         previously called
    */
   ResourceTransaction getCurrentTransaction();
}
