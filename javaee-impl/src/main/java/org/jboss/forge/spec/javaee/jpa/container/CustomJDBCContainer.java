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
public class CustomJDBCContainer implements PersistenceContainer
{
   @Inject
   private ShellPrintWriter writer;

   @Override
   public PersistenceUnitDef setupConnection(final PersistenceUnitDef unit, final JPADataSource dataSource)
   {
      unit.transactionType(TransactionType.RESOURCE_LOCAL);
      if (dataSource.getJndiDataSource() != null)
      {
         ShellMessages.info(writer, "Ignoring JNDI data-source [" + dataSource.getJndiDataSource() + "]");
      }

      if (!dataSource.hasNonDefaultDatabase())
      {
         throw new RuntimeException("Must specify database type for JDBC connections.");
      }
      if (Strings.isNullOrEmpty(dataSource.getDatabaseURL()))
      {
         throw new RuntimeException("Must specify database URL for JDBC connections.");
      }
      if (Strings.isNullOrEmpty(dataSource.getUsername()))
      {
         throw new RuntimeException("Must specify username for JDBC connections.");
      }
      if (Strings.isNullOrEmpty(dataSource.getPassword()))
      {
         throw new RuntimeException("Must specify password for JDBC connections.");
      }

      unit.nonJtaDataSource(null);
      unit.jtaDataSource(null);

      unit.property("javax.persistence.jdbc.driver", dataSource.getJdbcDriver());
      unit.property("javax.persistence.jdbc.url", dataSource.getDatabaseURL());
      unit.property("javax.persistence.jdbc.user", dataSource.getUsername());
      unit.property("javax.persistence.jdbc.password", dataSource.getPassword());

      return unit;
   }

   @Override
   public TransactionType getTransactionType()
   {
      return TransactionType.RESOURCE_LOCAL;
   }

}
