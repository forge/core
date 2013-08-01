/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.facets;

import java.util.Map;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface MetadataFacet extends ProjectFacet
{
   /**
    * Get the name of this {@link Project}.
    */
   String getProjectName();

   /**
    * Set the name of this {@link Project}.
    */
   void setProjectName(String name);

   /**
    * Get the top level package of this {@link Project}.
    */
   String getTopLevelPackage();

   /**
    * Set the top level package of this {@link Project}.
    */
   void setTopLevelPackage(String groupId);

   /**
    * Get the version of this {@link Project}.
    */
   String getProjectVersion();

   /**
    * Set the version of this {@link Project}.
    */
   void setProjectVersion(String version);

   /**
    * Return a handle to the final output dependency of this {@link Project}.
    * <p>
    * <b>NOTE:</b> Project may need to be built via {@link PackagingFacet#executeBuild(String...)} or
    * {@link PackagingFacet#createBuilder()} before this method will successfully return a result.
    */
   Dependency getOutputDependency();

   /**
    * Return a {@link Map} of all resolvable build properties in this {@link Project} and its parent hierarchy.
    * <p>
    * Properties can be used anywhere in a {@link Project} configuration and its dependencies, and will be expanded
    * during building to the resolved property value.
    */
   Map<String, String> getEffectiveProperties();

   /**
    * Return a {@link Map} of all build properties defined directly in this {@link Project}.
    * <p>
    * Properties can be used anywhere in a {@link Project} configuration and its dependencies, and will be expanded
    * during building to the resolved property value.
    */
   public Map<String, String> getDirectProperties();

   /**
    * Get a build property by name. Returns <code>null</code> if no such property exists in the {@link Project} and its
    * parent hierarchy.
    * <p>
    * Properties can be used anywhere in a {@link Project} configuration and its dependencies, and will be expanded
    * during building to the resolved property value.
    */
   String getEffectiveProperty(String name);

   /**
    * Get a build property by name. Returns <code>null</code> if no such property is defined directly in this project.
    * <p>
    * Properties can be used anywhere in a {@link Project} configuration and its dependencies, and will be expanded
    * during building to the resolved property value.
    */
   public String getDirectProperty(String name);

   /**
    * Set a build dependency property in the current project. This overrides any existing or inherited properties.
    * <p>
    * Properties can be used anywhere in a {@link Project} configuration and its dependencies, and will be expanded
    * during building to the resolved property value.
    */
   public void setProperty(String name, String value);

}
