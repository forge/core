/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.facets;

import java.util.Map;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface MetadataFacet extends ProvidedProjectFacet
{
   /**
    * Get the name of this {@link Project}.
    */
   String getProjectName();

   /**
    * Set the name of this {@link Project}.
    */
   MetadataFacet setProjectName(String name);

   /**
    * Get the top level package of this {@link Project}.
    * 
    * @deprecated use {@link MetadataFacet#getProjectGroupName()}
    */
   @Deprecated
   String getTopLevelPackage();

   /**
    * Get the project group name of this {@link Project}.
    */
   String getProjectGroupName();

   /**
    * Set the top level package of this {@link Project}.
    * 
    * @deprecated use {@link MetadataFacet#setProjectGroupName(String)}
    */
   @Deprecated
   MetadataFacet setTopLevelPackage(String groupId);

   /**
    * Set the top level package of this {@link Project}.
    */
   MetadataFacet setProjectGroupName(String groupId);

   /**
    * Get the version of this {@link Project}.
    */
   String getProjectVersion();

   /**
    * Set the version of this {@link Project}.
    */
   MetadataFacet setProjectVersion(String version);

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
   Map<String, String> getDirectProperties();

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
   String getDirectProperty(String name);

   /**
    * Set a build dependency property in the current project. This overrides any existing or inherited properties.
    * <p>
    * Properties can be used anywhere in a {@link Project} configuration and its dependencies, and will be expanded
    * during building to the resolved property value.
    */
   MetadataFacet setDirectProperty(String name, String value);

   /**
    * Remove a build property by name. (Build properties, such as ${my.version}, can be used anywhere in a dependency,
    * and will be expanded during building to their property value.)
    */
   String removeDirectProperty(String name);

   /**
    * Return the {@link ProjectProvider} being used in the current {@link Project}.
    */
   ProjectProvider getProjectProvider();

   /**
    * Returns <code>true</code> if the underlying {@link Project} metadata is in a valid state.
    */
   boolean isValid();

}
