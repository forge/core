/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectAssociationProvider;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;

/**
 * Setup parent-child relation of Maven projects.
 * 
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenMultiModuleProvider implements ProjectAssociationProvider
{
   @Override
   public void associate(final Project project, final Resource<?> parentResource)
   {
      if (canAssociate(project, parentResource))
      {
         ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
                  .get();
         Project parent = projectFactory.findProject(parentResource);
         MavenFacet parentMavenFacet = parent.getFacet(MavenFacet.class);
         Model parentPom = parentMavenFacet.getModel();

         if (parentPom.getPackaging().equalsIgnoreCase("pom"))
         {
            String moduleDir = project.getRoot().getFullyQualifiedName()
                     .substring(parent.getRoot().getFullyQualifiedName().length());
            if (moduleDir.startsWith(File.separator))
               moduleDir = moduleDir.substring(1);
            // If the module is already there, don't add
            if (parentPom.getModules().contains(moduleDir))
            {
               Logger.getLogger(getClass().getName())
                        .warning("Module '" + moduleDir + "' is already declared in the parent pom.xml");
            }
            else
            {
               parentPom.addModule(moduleDir);
               parentMavenFacet.setModel(parentPom);
            }
            MavenFacet projectMavenFacet = project.getFacet(MavenFacet.class);
            Model pom = projectMavenFacet.getModel();

            Parent projectParent = new Parent();
            String groupId = parentPom.getGroupId();
            if (groupId == null)
            {
               groupId = parentPom.getParent().getGroupId();
            }
            projectParent.setGroupId(groupId);
            projectParent.setArtifactId(parentPom.getArtifactId());

            String version = resolveVersion(parentPom);
            projectParent.setVersion(version);

            // Calculate parent relative path
            Path parentPomPath = Paths.get(parentMavenFacet.getModelResource().getFullyQualifiedName());
            Path childPath = Paths.get(project.getRoot().getFullyQualifiedName());
            Path relativePath = childPath.relativize(parentPomPath).normalize();

            projectParent.setRelativePath(relativePath.toString());

            // Reuse GroupId and version from parent
            pom.setGroupId(null);
            pom.setVersion(null);
            pom.setParent(projectParent);
            projectMavenFacet.setModel(pom);
         }
      }
   }

   private String resolveVersion(Model parent)
   {
      String version = parent.getVersion();
      if (Strings.isNullOrEmpty(version)
               && parent.getParent() != null
               && !Strings.isNullOrEmpty(parent.getParent().getVersion()))
      {
         version = parent.getParent().getVersion();
      }
      return version;
   }

   @Override
   public boolean canAssociate(final Project project, final Resource<?> parent)
   {
      return parent.getChild("pom.xml").exists() && project.getRoot().getChild("pom.xml").exists();
   }
}
