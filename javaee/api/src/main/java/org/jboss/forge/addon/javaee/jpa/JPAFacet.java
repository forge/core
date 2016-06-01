/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.util.List;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.Configurable;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;

/**
 * If installed, this {@link Project} supports features from the JPA specification.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
@FacetConstraint(ResourcesFacet.class)
public interface JPAFacet<DESCRIPTOR extends PersistenceCommonDescriptor> extends JavaEEFacet, Configurable<DESCRIPTOR>
{
   /**
    * Get this {@link Project}'s default package for @Entity objects
    */
   String getEntityPackage();

   /**
    * Get this {@link Project}'s default @Entity package directory.
    */
   DirectoryResource getEntityPackageDir();

   /**
    * Get a list of all @Entity classes in the default entity package.
    *
    * @see {@link #getEntityPackage()}, {@link #getEntityPackageDir()}
    */
   List<JavaClassSource> getAllEntities();
}
