/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee;

import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Facet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;

/**
 * If installed, this {@link Project} supports features from the JPA specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface PersistenceFacet extends Facet
{
   /**
    * Parse and return this {@link Project}'s persistence.xml file as a {@link PersistenceDescriptor}
    */
   PersistenceDescriptor getConfig();

   /**
    * Save the given {@link PersistenceDescriptor} as this {@link Project}'s persistence.xml file.
    */
   void saveConfig(final PersistenceDescriptor descriptor);

   /**
    * Get a reference to this {@link Project}'s persistence.xml file.
    */
   FileResource<?> getConfigFile();

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
