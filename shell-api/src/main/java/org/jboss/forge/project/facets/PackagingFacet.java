/*
 * JBoss, by Red Hat.
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
package org.jboss.forge.project.facets;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.build.ProjectBuilder;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.Resource;

/**
 * A Facet representing this project's Packaging (JAR, WAR, EAR, etc...)
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface PackagingFacet extends Facet
{
   /**
    * Set the packaging type currently in use by this project. For example, JAR, WAR,... etc.
    */
   void setPackagingType(PackagingType type);

   /**
    * Get the packaging type currently in use by this project. For example, JAR, WAR,... etc.
    */
   PackagingType getPackagingType();

   /**
    * Return the resource representing the fully built output artifact of this project. For example, if the project
    * builds a JAR file, this method must return the {@link Resource} representing that JAR file.
    */
   Resource<?> getFinalArtifact();

   /**
    * Return a new {@link ProjectBuilder} instance. This object is responsible for executing a build with custom
    * options.
    */
   ProjectBuilder createBuilder();

   /**
    * Trigger the underlying build system to perform a build with the given arguments or flags.
    * 
    * @return The final build artifact if building succeeded, otherwise return null
    * @see {@link #getFinalArtifact()}
    */
   Resource<?> executeBuild(String... args);

   /**
    * Get the final name of this project's build output artifact. This represents the name without file extension.
    */
   String getFinalName();

   /**
    * Set the final name of this project's build output artifact. This represents the name without file extension.
    */
   void setFinalName(String finalName);

}
