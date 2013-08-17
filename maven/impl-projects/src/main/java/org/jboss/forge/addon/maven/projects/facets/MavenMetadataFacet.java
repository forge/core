/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@RequiresFacet(MavenFacet.class)
public class MavenMetadataFacet extends AbstractFacet<Project> implements MetadataFacet
{
   @Override
   public String getProjectName()
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      return pom.getArtifactId();
   }

   @Override
   public String getProjectVersion()
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
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
   public void setProjectVersion(String version)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      pom.setVersion(version);
      mvn.setPOM(pom);
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
   public void setProjectName(final String name)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      pom.setArtifactId(name);
      mvn.setPOM(pom);
   }

   @Override
   public void setTopLevelPackage(final String groupId)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      pom.setGroupId(groupId);
      mvn.setPOM(pom);
   }

   @Override
   public String getTopLevelPackage()
   {
      Model pom = getFaceted().getFacet(MavenFacet.class).getPOM();
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
      return DependencyBuilder.create().setGroupId(getTopLevelPackage()).setArtifactId(getProjectName())
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
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      Map<String, String> result = new HashMap<String, String>();
      for (Entry<Object, Object> o : properties.entrySet())
      {
         result.put((String) o.getKey(), (String) o.getValue());
      }
      return result;
   }

   @Override
   public void setDirectProperty(final String name, final String value)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      properties.put(name, value);
      maven.setPOM(pom);
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
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      String result = (String) properties.remove(name);
      maven.setPOM(pom);
      return result;
   }
   
}
