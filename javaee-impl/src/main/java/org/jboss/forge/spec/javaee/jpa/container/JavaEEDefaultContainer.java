/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
