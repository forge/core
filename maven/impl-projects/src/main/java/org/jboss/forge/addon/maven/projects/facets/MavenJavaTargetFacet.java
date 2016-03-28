/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaTargetFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint({ MavenFacet.class, PackagingFacet.class })
public class MavenJavaTargetFacet extends AbstractFacet<Project> implements JavaTargetFacet
{
   @Override
   public List<DirectoryResource> getTargetDirectories()
   {
      List<DirectoryResource> result = new ArrayList<>();
      result.add(getTargetDirectory());
      result.add(getTestTargetDirectory());
      return result;
   }

   @Override
   public DirectoryResource getTargetDirectory()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenFacet.getModel().getBuild();
      String targetFolderName;
      if (build != null && build.getOutputDirectory() != null)
      {
         targetFolderName = mavenFacet.resolveProperties(build.getOutputDirectory());
      }
      else
      {
         targetFolderName = "target" + File.separator + "classes";
      }
      DirectoryResource projectRoot = getFaceted().getRoot().reify(DirectoryResource.class);
      return projectRoot.getChildDirectory(targetFolderName);
   }

   @Override
   public DirectoryResource getTestTargetDirectory()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenFacet.getModel().getBuild();
      String targetFolderName;
      if (build != null && build.getTestOutputDirectory() != null)
      {
         targetFolderName = mavenFacet.resolveProperties(build.getTestOutputDirectory());
      }
      else
      {
         targetFolderName = "target" + File.separator + "test-classes";
      }
      DirectoryResource projectRoot = getFaceted().getRoot().reify(DirectoryResource.class);
      return projectRoot.getChildDirectory(targetFolderName);
   }

   @Override
   public boolean install()
   {
      PackagingFacet packagingFacet = getFaceted().getFacet(PackagingFacet.class);
      packagingFacet.createBuilder().quiet(true).runTests(false).build();
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      return getTargetDirectory().exists();
   }
}
