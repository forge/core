/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.projects.facets;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.facets.BaseFacet;
import org.jboss.forge.maven.projects.MavenFacet;
import org.jboss.forge.maven.projects.MavenFacetImpl;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.facets.MetadataFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class MavenMetadataFacet extends BaseFacet<Project> implements MetadataFacet
{
   @Override
   public String getProjectName()
   {
      return ((MavenFacetImpl) getOrigin().getFacet(MavenFacet.class)).getPartialProjectBuildingResult().getProject()
               .getArtifactId();
   }

   @Override
   public String getProjectVersion()
   {
      return ((MavenFacetImpl) getOrigin().getFacet(MavenFacet.class)).getPartialProjectBuildingResult().getProject()
               .getVersion();
   }

   @Override
   public void setOrigin(final Project project)
   {
      this.setOrigin(project);
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
      String groupId = getOrigin().getFacet(MavenFacet.class).getPOM().getGroupId();

      // If groupId is null, try to grab the parent's groupId
      if (groupId == null)
      {
         Parent parent = getOrigin().getFacet(MavenFacet.class).getPOM().getParent();
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
