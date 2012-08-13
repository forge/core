/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.container;

import javax.inject.Inject;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceContainer;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.TransactionType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NonJTAContainer implements PersistenceContainer
{
   @Inject
   private ShellPrintWriter writer;

   @Override
   public PersistenceUnitDef setupConnection(final PersistenceUnitDef unit, final JPADataSource dataSource)
   {
      unit.transactionType(TransactionType.RESOURCE_LOCAL);
      if (Strings.isNullOrEmpty(dataSource.getJndiDataSource()))
      {
         throw new RuntimeException("Must specify a JNDI data-source.");
      }
      if (dataSource.hasJdbcConnectionInfo())
      {
         ShellMessages.info(writer, "Ignoring jdbc connection info [" + dataSource.getJdbcConnectionInfo() + "]");
      }

      unit.nonJtaDataSource(dataSource.getJndiDataSource());
      unit.jtaDataSource(null);

      return unit;
   }

   @Override
   public TransactionType getTransactionType()
   {
      return TransactionType.RESOURCE_LOCAL;
   }
}
