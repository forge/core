/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnitTransactionType;
import org.jboss.shrinkwrap.descriptor.api.persistence20.Properties;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CustomJDBCContainer implements PersistenceContainer
{

   @Override
   public PersistenceUnit<PersistenceDescriptor> setupConnection(PersistenceUnit<PersistenceDescriptor> unit,
            JPADataSource dataSource)
   {
      unit.transactionType(PersistenceUnitTransactionType._RESOURCE_LOCAL);
      unit.nonJtaDataSource(null);
      unit.jtaDataSource(null);

      Properties<PersistenceUnit<PersistenceDescriptor>> properties = unit.getOrCreateProperties();
      properties.createProperty().name("javax.persistence.jdbc.driver").value(dataSource.getJdbcDriver());
      properties.createProperty().name("javax.persistence.jdbc.url").value(dataSource.getDatabaseURL());
      properties.createProperty().name("javax.persistence.jdbc.user").value(dataSource.getUsername());
      properties.createProperty().name("javax.persistence.jdbc.password").value(dataSource.getPassword());

      return unit;
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
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
   }

   @Override
   public String getName()
   {
      return "Custom JDBC";
   }
}
