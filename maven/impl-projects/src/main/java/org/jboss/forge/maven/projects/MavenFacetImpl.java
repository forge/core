/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import org.jboss.forge.facets.BaseFacet;
import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenFacetImpl extends BaseFacet<Project> implements ProjectFacet, MavenFacet
{

   @Override
   public void setOrigin(Project project)
   {
      super.setOrigin(project);
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         MavenPomResource pom = getPomResource();
         if (!pom.createNewFile())
            throw new IllegalStateException("Could not create POM file.");

         pom.setContents(getClass().getClassLoader().getResourceAsStream("/pom-template.xml"));
      }
      return isInstalled();
   }

   @Override
   public MavenPomResource getPomResource()
   {
      return (MavenPomResource) getOrigin().getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);
   }

   @Override
   public boolean isInstalled()
   {
      MavenPomResource pom = getPomResource();
      return pom != null && pom.exists();
   }

}
