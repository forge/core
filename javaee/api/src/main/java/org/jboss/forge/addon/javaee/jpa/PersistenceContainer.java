/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.projects.stacks.StackSupport;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Performs configuration on a {@link JPADataSource} to ensure it is properly set up for the this implementation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface PersistenceContainer extends StackSupport
{
   /**
    * Return the name of this container
    */
   String getName(boolean isGUI);

   /**
    * Validate against the supplied datastore
    */
   void validate(JPADataSource dataSource) throws Exception;

   /**
    * Set up the connection info.
    */
   @SuppressWarnings("rawtypes")
   PersistenceUnitCommon setupConnection(PersistenceUnitCommon unit,
            JPADataSource dataSource);

   /**
    * @return true if this {@link PersistenceContainer} requires a DataSource to function properly
    */
   boolean isDataSourceRequired();

   @Override
   default boolean supports(Stack stack)
   {
      return true;
   }

}
