/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Alias("forge.maven.MetadataFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenMetadataFacet extends BaseFacet implements MetadataFacet
{
   private Project project;

   @Override
   public String getProjectName()
   {
      return project.getFacet(MavenCoreFacet.class).getPartialProjectBuildingResult().getProject().getArtifactId();
   }

   @Override
   public String getProjectVersion()
   {
      return project.getFacet(MavenCoreFacet.class).getPartialProjectBuildingResult().getProject().getVersion();
   }

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(final Project project)
   {
      this.project = project;
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return project.hasFacet(MavenCoreFacet.class);
   }

   @Override
   public void setProjectName(final String name)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);
      Model pom = mvn.getPOM();
      pom.setArtifactId(name);
      mvn.setPOM(pom);
   }

   @Override
   public void setTopLevelPackage(final String groupId)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);
      Model pom = mvn.getPOM();
      pom.setGroupId(groupId);
      mvn.setPOM(pom);
   }

   @Override
   public String getTopLevelPackage()
   {
      String groupId = project.getFacet(MavenCoreFacet.class).getPOM().getGroupId();

      // If groupId is null, try to grab the parent's groupId
      if (groupId == null) {
          Parent parent = project.getFacet(MavenCoreFacet.class).getPOM().getParent();
          if (parent != null) {
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
