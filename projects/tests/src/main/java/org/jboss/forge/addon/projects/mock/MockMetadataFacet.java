/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.mock;

import java.util.Collections;
import java.util.Map;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class MockMetadataFacet extends AbstractFacet<Project> implements MetadataFacet
{
   private final ProjectProvider buildSystem;

   public MockMetadataFacet()
   {
      this.buildSystem = null;
   }

   public MockMetadataFacet(MockProject mockProject, ProjectProvider buildSystem)
   {
      setFaceted(mockProject);
      this.buildSystem = buildSystem;
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   @Override
   public String getProjectName()
   {
      return "mock";
   }

   @Override
   public MetadataFacet setProjectName(String name)
   {
      return this;
   }

   @Override
   public String getTopLevelPackage()
   {
      return null;
   }

   @Override
   public String getProjectGroupName()
   {
      return null;
   }

   @Override
   public MetadataFacet setTopLevelPackage(String groupId)
   {
      return null;
   }

   @Override
   public MetadataFacet setProjectGroupName(String groupId)
   {
      return null;
   }

   @Override
   public String getProjectVersion()
   {
      return "1.0.0-SNAPSHOT";
   }

   @Override
   public MetadataFacet setProjectVersion(String version)
   {
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
      return Collections.emptyMap();
   }

   @Override
   public Map<String, String> getDirectProperties()
   {
      return Collections.emptyMap();
   }

   @Override
   public String getEffectiveProperty(String name)
   {
      return null;
   }

   @Override
   public String getDirectProperty(String name)
   {
      return null;
   }

   @Override
   public MetadataFacet setDirectProperty(String name, String value)
   {
      return this;
   }

   @Override
   public String removeDirectProperty(String name)
   {
      return null;
   }

   @Override
   public ProjectProvider getProjectProvider()
   {
      return buildSystem;
   }

   @Override
   public boolean isValid()
   {
      return true;
   }

}
