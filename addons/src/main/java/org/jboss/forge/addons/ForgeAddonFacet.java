/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons;

import javax.inject.Inject;

import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.projects.MavenPluginFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ForgeAddonFacet extends AbstractFacet<Project> implements ProjectFacet
{

   @Inject
   private AddonRegistry addonRegistry;

   @Override
   public boolean install()
   {
      Project project = getOrigin();
      // TODO: Change to @RequiresFacet
      if (!project.hasFacet(MavenPluginFacet.class))
      {
         MavenPluginFacet facet = addonRegistry.getExportedInstance(MavenPluginFacet.class).get();
         facet.setOrigin(project);
         project.install(facet);
      }
      MavenPluginFacet pluginFacet = getOrigin().getFacet(MavenPluginFacet.class);
      MavenPlugin forgeAddon = MavenPluginBuilder
               .create()
               .setCoordinate(CoordinateBuilder.create().setGroupId("org.apache.maven.plugins")
                        .setArtifactId("maven-compiler-plugin"))
               .addExecution(
                        ExecutionBuilder
                                 .create()
                                 .setId("create-forge-addon")
                                 .setPhase("package")
                                 .addGoal("jar")
                                 .setConfig(
                                          ConfigurationBuilder.create().addConfigurationElement(
                                                   ConfigurationElementBuilder.create().setName("classifier")
                                                            .setText("forge-addon"))));
      pluginFacet.addPlugin(forgeAddon);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return false;
   }

   @Override
   public void setOrigin(Project origin)
   {
      super.setOrigin(origin);
   }
}
