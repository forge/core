/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class JavaEEDefaultContainer implements PersistenceContainer
{

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon setupConnection(PersistenceUnitCommon unit,
            JPADataSource dataSource)
   {
      // ShellMessages.info(getWriter(), "Setting transaction-type=\"JTA\"");
      unit.transactionType("JTA");

      if (dataSource.getDatabase() == null)
      {
         // ShellMessages.info(getWriter(), "Using example database type [" + getDefaultDatabaseType() + "]");
         dataSource.setDatabase(getDefaultDatabaseType());
      }

      if (dataSource.getJndiDataSource() != null)
      {
         // ShellMessages.info(getWriter(), "Overriding example datasource with [" + dataSource.getJndiDataSource() +
         // "]");
         unit.jtaDataSource(dataSource.getJndiDataSource());
      }
      else
      {
         // ShellMessages.info(getWriter(), "Using example data source [" + getDefaultDataSource() + "]");
         unit.jtaDataSource(getDefaultDataSource());
      }

      return unit;
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
      if (dataSource.hasJdbcConnectionInfo())
      {
         throw new IllegalStateException(
                  "Cannot specify jdbc connection info when using container managed datasources ["
                           + dataSource.getJdbcConnectionInfo() + "]");
      }
   }

   public abstract String getDefaultDataSource();

   public abstract DatabaseType getDefaultDatabaseType();

   @Override
   public boolean isDataSourceRequired()
   {
      return true;
   }

   @Override
   public boolean supports(Stack stack)
   {
      return stack.supports(JavaEEFacet.class);
   }
}
