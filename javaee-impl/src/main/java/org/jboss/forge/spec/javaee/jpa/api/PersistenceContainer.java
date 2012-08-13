/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.api;

import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.TransactionType;

/**
 * Performs configuration on a {@link JPADataSource} to ensure it is properly set up for the this implementation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface PersistenceContainer
{
   /**
    * Set up the connection info.
    */
   PersistenceUnitDef setupConnection(PersistenceUnitDef unit, JPADataSource dataSource);

   /**
    * Get this the supported {@link TransactionType} of this {@link PersistenceContainer}
    */
   TransactionType getTransactionType();
}
