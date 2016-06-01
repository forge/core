/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.roaster.model.util.Strings;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class NonJTAContainer implements PersistenceContainer
{
   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon setupConnection(PersistenceUnitCommon unit,
            JPADataSource dataSource)
   {
      unit.transactionType("RESOURCE_LOCAL");
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
   public boolean isDataSourceRequired()
   {
      return true;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "Non-JTA Container" : "NON_JTA";
   }

   @Override
   public boolean supports(Stack stack)
   {
      return stack.supports(JavaSourceFacet.class);
   }
}
