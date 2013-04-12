/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectAssociationProvider;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.resource.DirectoryResource;

/**
 * Setup parent-child relation of Maven projects.
 * 
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenMultiModuleProvider implements ProjectAssociationProvider
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public void associate(final Project project, final DirectoryResource parentDir)
   {
      if (canAssociate(project, parentDir))
      {
         Project parent = projectFactory.findProject(parentDir);
         MavenFacet parentMavenFacet = parent.getFacet(MavenFacet.class);
         Model parentPom = parentMavenFacet.getPOM();
         parentPom.setPackaging("pom");

         String moduleDir = project.getProjectRoot().getFullyQualifiedName()
                  .substring(parent.getProjectRoot().getFullyQualifiedName().length());
         if (moduleDir.startsWith("/"))
            moduleDir = moduleDir.substring(1);
         
         parentPom.addModule(moduleDir);
         parentMavenFacet.setPOM(parentPom);

         MavenFacet projectMavenFacet = project.getFacet(MavenFacet.class);
         Model pom = projectMavenFacet.getPOM();

         Parent projectParent = new Parent();
         projectParent.setGroupId(parentPom.getGroupId());
         projectParent.setArtifactId(parentPom.getArtifactId());
         projectParent.setVersion(parentPom.getVersion());

         pom.setParent(projectParent);
         projectMavenFacet.setPOM(pom);
      }
   }

   @Override
   public boolean canAssociate(final Project project, final DirectoryResource parent)
   {
      return parent.getChild("pom.xml").exists() && project.getProjectRoot().getChild("pom.xml").exists();
   }
}
