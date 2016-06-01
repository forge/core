/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.Project;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Performs configuration of a {@link JPADataSource} to ensure it is properly set up for this implementation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface PersistenceProvider
{

   /**
    * Get the name of this provider
    */
   String getName();

   /**
    * Get the classname of this provider.
    */
   String getProvider();

   /**
    * Configure the {@link PersistenceUnitCommon} and {@link JPADataSource}.
    */
   @SuppressWarnings("rawtypes")
   PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds,
            Project project);

   /**
    * List any dependencies required by this provider.
    */
   List<Dependency> listDependencies();

   /**
    * The JPA 2 meta model provider related to this persistence provider.
    */
   MetaModelProvider getMetaModelProvider();

   /**
    * Validate against the supplied datastore
    * 
    * @throws any exception if the supplied datasource state is invalid
    */
   void validate(JPADataSource dataSource) throws Exception;

}
