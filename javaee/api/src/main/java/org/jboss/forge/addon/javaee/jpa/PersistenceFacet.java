/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.util.List;

import org.jboss.forge.addon.javaee.Configurable;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;

/**
 * If installed, this {@link Project} supports features from the JPA specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface PersistenceFacet extends JavaEEFacet, Configurable<PersistenceDescriptor>
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
   List<JavaClass> getAllEntities();
}
