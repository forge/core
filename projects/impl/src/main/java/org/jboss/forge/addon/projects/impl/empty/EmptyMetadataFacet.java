/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.empty;

import java.util.Collections;
import java.util.Map;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class EmptyMetadataFacet extends AbstractFacet<Project> implements MetadataFacet
{
   private EmptyProjectProvider projectProvider;

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getFaceted() instanceof EmptyProject;
   }

   @Override
   public String getProjectName()
   {
      return getFaceted().getRoot().getName();
   }

   @Override
   public MetadataFacet setProjectName(String name)
   {
      return this;
   }

   @Override
   public String getTopLevelPackage()
   {
      return getFaceted().getRoot().getParent().getFullyQualifiedName();
   }

   @Override
   public String getProjectGroupName()
   {
      return getTopLevelPackage();
   }

   @Override
   public MetadataFacet setTopLevelPackage(String groupId)
   {
      // noop
      return this;
   }

   @Override
   public MetadataFacet setProjectGroupName(String groupId)
   {
      return null;
   }

   @Override
   public String getProjectVersion()
   {
      return null;
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
      return projectProvider;
   }

   public void setProjectProvider(EmptyProjectProvider projectProvider)
   {
      this.projectProvider = projectProvider;
   }

   @Override
   public boolean isValid()
   {
      return isInstalled();
   }

}
