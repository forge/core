/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.generic.facets;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.generic.GenericProjectProvider;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Generic implementation for {@link MetadataFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(ConfigurationFacet.class)
public class GenericMetadataFacet extends AbstractFacet<Project> implements MetadataFacet
{
   private static final String PROJECT_NAME_KEY = "generic-project-name";
   private static final String PROJECT_VERSION_KEY = "generic-project-version";
   private static final String PROJECT_GROUP_NAME_KEY = "generic-project-groupName";
   private static final String PROPERTIES_SUBSET_KEY = "generic-project-properties";

   private Configuration configuration;

   @Override
   public String getProjectName()
   {
      return getConfiguration().getString(PROJECT_NAME_KEY);
   }

   @Override
   public MetadataFacet setProjectName(String name)
   {
      getConfiguration().setProperty(PROJECT_NAME_KEY, name);
      return this;
   }

   @Override
   public String getTopLevelPackage()
   {
      return getProjectGroupName();
   }

   @Override
   public String getProjectGroupName()
   {
      return getConfiguration().getString(PROJECT_GROUP_NAME_KEY);
   }

   @Override
   public MetadataFacet setTopLevelPackage(String groupId)
   {
      return setProjectGroupName(groupId);
   }

   @Override
   public MetadataFacet setProjectGroupName(String groupId)
   {
      getConfiguration().setProperty(PROJECT_GROUP_NAME_KEY, groupId);
      return this;
   }

   @Override
   public String getProjectVersion()
   {
      return getConfiguration().getString(PROJECT_VERSION_KEY);
   }

   @Override
   public MetadataFacet setProjectVersion(String version)
   {
      getConfiguration().setProperty(PROJECT_VERSION_KEY, version);
      return this;
   }

   @Override
   public Dependency getOutputDependency()
   {
      return null;
   }

   @Override
   public Map<String, String> getEffectiveProperties()
   {
      return getDirectProperties();
   }

   @Override
   public Map<String, String> getDirectProperties()
   {
      Configuration subset = getConfiguration().subset(PROPERTIES_SUBSET_KEY);
      Map<String, String> map = new LinkedHashMap<>();
      subset.getKeys().forEachRemaining((key) -> map.put(key, subset.getString(key)));
      return map;
   }

   @Override
   public String getEffectiveProperty(String name)
   {
      return getEffectiveProperties().get(name);
   }

   @Override
   public String getDirectProperty(String name)
   {
      return getDirectProperties().get(name);
   }

   @Override
   public MetadataFacet setDirectProperty(String name, String value)
   {
      Configuration subset = getConfiguration().subset(PROPERTIES_SUBSET_KEY);
      subset.setProperty(name, value);
      return this;
   }

   @Override
   public String removeDirectProperty(String name)
   {
      Configuration subset = getConfiguration().subset(PROPERTIES_SUBSET_KEY);
      String value = subset.getString(name);
      subset.clearProperty(name);
      return value;
   }

   @Override
   public ProjectProvider getProjectProvider()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), GenericProjectProvider.class).get();
   }

   @Override
   public boolean isValid()
   {
      return isInstalled();
   }

   private Configuration getConfiguration()
   {
      if (configuration == null)
      {
         configuration = getFaceted().getFacet(ConfigurationFacet.class).getConfiguration();
      }
      return configuration;
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return isInstalled(getConfiguration());
   }

   public static boolean isInstalled(Configuration configuration)
   {
      return configuration.containsKey(PROJECT_NAME_KEY);
   }
}