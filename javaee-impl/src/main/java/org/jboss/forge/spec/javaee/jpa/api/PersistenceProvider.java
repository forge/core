/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.api;

import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

/**
 * Performs configuration of a {@link JPADataSource} to ensure it is properly set up for this implementation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface PersistenceProvider
{
   /**
    * Get the name of this provider.
    */
   String getProvider();

   /**
    * Configure the {@link PersistenceUnitDef} and {@link JPADataSource}.
    */
   PersistenceUnitDef configure(PersistenceUnitDef unit, JPADataSource ds);

   /**
    * List any dependencies required by this provider.
    */
   List<Dependency> listDependencies();
}
