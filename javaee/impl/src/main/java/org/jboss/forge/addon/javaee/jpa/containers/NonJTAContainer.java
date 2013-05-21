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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NonJTAContainer implements PersistenceContainer
{

   @Override
   public PersistenceUnit<PersistenceDescriptor> setupConnection(PersistenceUnit<PersistenceDescriptor> unit,
            JPADataSource dataSource)
   {
      unit.transactionType(PersistenceUnitTransactionType._RESOURCE_LOCAL);
      if (dataSource.hasJdbcConnectionInfo())
      {
         // ShellMessages.info(writer, "Ignoring jdbc connection info [" + dataSource.getJdbcConnectionInfo() + "]");
      }

      unit.nonJtaDataSource(dataSource.getJndiDataSource());
      unit.jtaDataSource(null);

      return unit;
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
      if (Strings.isNullOrEmpty(dataSource.getJndiDataSource()))
      {
         throw new RuntimeException("Must specify a JNDI data-source.");
      }
   }

   @Override
   public String getName()
   {
      return "Non-JTA Container";
   }
}
