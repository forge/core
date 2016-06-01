/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(MavenFacet.class)
public class MavenMetadataFacet extends AbstractFacet<Project>implements MetadataFacet
{
   @Override
   public ProjectProvider getProjectProvider()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), MavenBuildSystem.class).get();
   }

   @Override
   public String getProjectName()
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getModel();
      return pom.getArtifactId();
   }

   @Override
   public String getProjectVersion()
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getModel();
      String version = pom.getVersion();
      if (version == null)
      {
         Parent parent = pom.getParent();
         if (parent != null)
         {
            version = parent.getVersion();
         }
      }
      return version;
   }

   @Override
   public MavenMetadataFacet setProjectVersion(String version)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getModel();
      pom.setVersion(version);
      mvn.setModel(pom);
      return this;
   }

   @Override
   public void setFaceted(final Project project)
   {
      super.setFaceted(project);
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
   public MavenMetadataFacet setProjectName(final String name)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getModel();
      pom.setArtifactId(name);
      mvn.setModel(pom);
      return this;
   }

   @Override
   @Deprecated
   public MavenMetadataFacet setTopLevelPackage(final String groupId)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getModel();
      pom.setGroupId(groupId);
      mvn.setModel(pom);
      return this;
   }

   @Override
   public MetadataFacet setProjectGroupName(String groupId)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getModel();
      pom.setGroupId(groupId);
      mvn.setModel(pom);
      return this;
   }

   @Override
   public String getProjectGroupName()
   {
      Model pom = getFaceted().getFacet(MavenFacet.class).getModel();
      String groupId = pom.getGroupId();

      // If groupId is null, try to grab the parent's groupId
      if (groupId == null)
      {
         Parent parent = pom.getParent();
         if (parent != null)
         {
            groupId = parent.getGroupId();
         }
      }
      return groupId;
   }

   @Override
   @Deprecated
   public String getTopLevelPackage()
   {
      Model pom = getFaceted().getFacet(MavenFacet.class).getModel();
      String groupId = pom.getGroupId();

      // If groupId is null, try to grab the parent's groupId
      if (groupId == null)
      {
         Parent parent = pom.getParent();
         if (parent != null)
         {
            groupId = parent.getGroupId();
         }
      }
      return groupId;
   }

   @Override
   public Dependency getOutputDependency()
   {
      return DependencyBuilder.create().setGroupId(getProjectGroupName()).setArtifactId(getProjectName())
               .setVersion(getProjectVersion());
   }

   @Override
   public Map<String, String> getEffectiveProperties()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      return maven.getProperties();
   }

   @Override
   public Map<String, String> getDirectProperties()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();

      Properties properties = pom.getProperties();
      Map<String, String> result = new HashMap<>();
      for (Entry<Object, Object> o : properties.entrySet())
      {
         result.put((String) o.getKey(), (String) o.getValue());
      }
      return result;
   }

   @Override
   public MavenMetadataFacet setDirectProperty(final String name, final String value)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();

      Properties properties = pom.getProperties();
      properties.put(name, value);
      maven.setModel(pom);
      return this;
   }

   @Override
   public String getDirectProperty(final String name)
   {
      return getDirectProperties().get(name);
   }

   @Override
   public String getEffectiveProperty(final String name)
   {
      return getEffectiveProperties().get(name);
   }

   @Override
   public String removeDirectProperty(final String name)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();

      Properties properties = pom.getProperties();
      String result = (String) properties.remove(name);
      maven.setModel(pom);
      return result;
   }

   @Override
   public boolean isValid()
   {
      return getFaceted().getFacet(MavenFacet.class).isModelValid();
   }

}
