/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
