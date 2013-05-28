/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class MavenMetadataFacet extends AbstractFacet<Project> implements MetadataFacet
{
   @Override
   public String getProjectName()
   {
      MavenFacet mvn = getOrigin().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      return pom.getArtifactId();
   }

   @Override
   public String getProjectVersion()
   {
      MavenFacet mvn = getOrigin().getFacet(MavenFacet.class);
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
      MavenFacet mvn = getOrigin().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      pom.setVersion(version);
      mvn.setPOM(pom);
   }

   @Override
   public void setOrigin(final Project project)
   {
      super.setOrigin(project);
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getOrigin().hasFacet(MavenFacet.class);
   }

   @Override
   public void setProjectName(final String name)
   {
      MavenFacet mvn = getOrigin().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      pom.setArtifactId(name);
      mvn.setPOM(pom);
   }

   @Override
   public void setTopLevelPackage(final String groupId)
   {
      MavenFacet mvn = getOrigin().getFacet(MavenFacet.class);
      Model pom = mvn.getPOM();
      pom.setGroupId(groupId);
      mvn.setPOM(pom);
   }

   @Override
   public String getTopLevelPackage()
   {
      Model pom = getOrigin().getFacet(MavenFacet.class).getPOM();
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
}
