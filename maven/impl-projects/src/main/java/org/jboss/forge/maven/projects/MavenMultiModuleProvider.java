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
         MavenFacet parentMCF = parent.getFacet(MavenFacet.class);
         Model parentPom = parentMCF.getPOM();
         parentPom.setPackaging("pom");
         parentPom.addModule(project.getProjectRoot().toString());
         parentMCF.setPOM(parentPom);

         MavenFacet mcf = project.getFacet(MavenFacet.class);
         Model pom = mcf.getPOM();

         Parent parentEntry = new Parent();
         parentEntry.setGroupId(parentPom.getGroupId());
         parentEntry.setArtifactId(parentPom.getArtifactId());
         parentEntry.setVersion(parentPom.getVersion());

         pom.setParent(parentEntry);
         mcf.setPOM(pom);
      }
   }

   @Override
   public boolean canAssociate(final Project project, final DirectoryResource parent)
   {
      return parent.getChild("pom.xml").exists() && project.getProjectRoot().getChild("pom.xml").exists();
   }
}
