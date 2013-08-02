/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.facets;

import java.util.List;

import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;
import org.jboss.forge.addon.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.addon.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.addon.maven.plugins.Execution;
import org.jboss.forge.addon.maven.plugins.ExecutionBuilder;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * Adds the Furnace Maven Plugin to the build descriptor in order to generate the DOT file automatically.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RequiresFacet(FurnaceVersionFacet.class)
public class FurnacePluginFacet extends AbstractFacet<Project> implements ProjectFacet
{
   private static final CoordinateBuilder FURNACE_PLUGIN_COORDINATE = CoordinateBuilder.create()
            .setGroupId("org.jboss.forge.furnace")
            .setArtifactId("furnace-maven-plugin")
            .setVersion(FurnaceVersionFacet.VERSION_PROPERTY);

   @Override
   public boolean install()
   {
      MavenPluginFacet pluginFacet = getFaceted().getFacet(MavenPluginFacet.class);
      MavenPluginBuilder plugin = MavenPluginBuilder
               .create()
               .setCoordinate(FURNACE_PLUGIN_COORDINATE)
               .addExecution(
                        ExecutionBuilder
                                 .create()
                                 .setId("generate-dot")
                                 .setPhase("prepare-package")
                                 .addGoal("generate-dot")
                                 .setConfig(ConfigurationBuilder.create().addConfigurationElement(
                                          ConfigurationElementBuilder.create().setName("attach")
                                                   .setText("true"))));
      pluginFacet.addPlugin(plugin);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      boolean installed = false;
      if (getFaceted().hasFacet(MavenPluginFacet.class)
               && getFaceted().getFacet(MavenPluginFacet.class).hasPlugin(FURNACE_PLUGIN_COORDINATE))
      {
         List<Execution> executions = getFaceted().getFacet(MavenPluginFacet.class).getPlugin(FURNACE_PLUGIN_COORDINATE)
                  .listExecutions();
         for (Execution execution : executions)
         {
            if ("generate-dot".equals(execution.getId())
                     && "true".equals(execution.getConfig().getConfigurationElement("attach").getText()))
            {
               installed = true;
               break;
            }
         }
      }
      return installed;
   }
}
