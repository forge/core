/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.EnterpriseResourcesFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;

/**
 * Implementation of {@link EnterpriseResourcesFacet}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint({ MavenPluginFacet.class, PackagingFacet.class })
public class MavenEnterpriseResourcesFacet extends AbstractFacet<Project> implements EnterpriseResourcesFacet
{
   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         Project project = getFaceted();
         project.getFacet(PackagingFacet.class).setPackagingType("ear");
         MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
         Coordinate mvnEarPluginDep = CoordinateBuilder.create().setGroupId("org.apache.maven.plugins")
                  .setArtifactId("maven-ear-plugin");
         MavenPlugin plugin;
         if (!plugins.hasPlugin(mvnEarPluginDep))
         {
            plugin = MavenPluginBuilder.create().setCoordinate(mvnEarPluginDep);
            plugins.addPlugin(plugin);
         }
      }
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getFaceted();
      String packagingType = project.getFacet(PackagingFacet.class).getPackagingType();

      return packagingType.equals("ear");
   }

}
