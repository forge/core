/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.container;

import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.spec.javaee.jpa.api.DatabaseType;
import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceContainer;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.TransactionType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class JavaEEDefaultContainer implements PersistenceContainer
{

   @Override
   public PersistenceUnitDef setupConnection(final PersistenceUnitDef unit, final JPADataSource dataSource)
   {
      ShellMessages.info(getWriter(), "Setting transaction-type=\"JTA\"");
      unit.transactionType(TransactionType.JTA);

      if (dataSource.getDatabase() == null)
      {
         ShellMessages.info(getWriter(), "Using example database type [" + getDefaultDatabaseType() + "]");
         dataSource.setDatabase(getDefaultDatabaseType());
      }

      if (dataSource.getJndiDataSource() != null)
      {
         ShellMessages.info(getWriter(), "Overriding example datasource with [" + dataSource.getJndiDataSource() + "]");
         unit.jtaDataSource(dataSource.getJndiDataSource());
      }
      else
      {
         ShellMessages.info(getWriter(), "Using example data source [" + getDefaultDataSource() + "]");
         unit.jtaDataSource(getDefaultDataSource());
      }

      if (dataSource.hasJdbcConnectionInfo())
      {
         throw new IllegalStateException(
                  "Cannot specify jdbc connection info when using container managed datasources ["
                           + dataSource.getJdbcConnectionInfo() + "]");
      }

      return unit;
   }

   @Override
   public TransactionType getTransactionType()
   {
      return TransactionType.JTA;
   }

   protected abstract String getDefaultDataSource();

   protected abstract DatabaseType getDefaultDatabaseType();

   protected abstract ShellPrintWriter getWriter();
}
