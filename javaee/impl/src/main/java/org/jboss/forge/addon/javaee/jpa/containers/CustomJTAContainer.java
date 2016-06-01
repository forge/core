/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CustomJTAContainer implements PersistenceContainer
{

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon setupConnection(PersistenceUnitCommon unit,
            JPADataSource dataSource)
   {
      unit.transactionType("JTA");
      unit.jtaDataSource(dataSource.getJndiDataSource());
      unit.nonJtaDataSource(null);

      return unit;
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
      if ((dataSource.getJndiDataSource() == null) || dataSource.getJndiDataSource().trim().isEmpty())
      {
         throw new RuntimeException("Must specify a JTA data-source.");
      }
   }

   @Override
   public boolean isDataSourceRequired()
   {
      return true;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "Custom JTA" : "CUSTOM_JTA";
   }
}