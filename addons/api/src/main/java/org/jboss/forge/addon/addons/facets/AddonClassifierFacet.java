/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.facets;

import java.util.List;

import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.addon.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.addon.maven.plugins.Execution;
import org.jboss.forge.addon.maven.plugins.ExecutionBuilder;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.furnace.lifecycle.AddonLifecycleProvider;

/**
 * Ensures that a project is configured as a forge-addon with access to the Furnace APIs, and provides a default
 * {@link AddonLifecycleProvider}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonClassifierFacet extends AbstractFacet<Project> implements ProjectFacet
{
   private static final CoordinateBuilder JAR_PLUGIN_COORDINATE = CoordinateBuilder.create()
            .setGroupId("org.apache.maven.plugins")
            .setArtifactId("maven-jar-plugin")
            .setVersion("2.6");

   @Override
   public boolean install()
   {
      MavenPluginFacet pluginFacet = getFaceted().getFacet(MavenPluginFacet.class);
      MavenPluginBuilder plugin = MavenPluginBuilder
               .create()
               .setCoordinate(JAR_PLUGIN_COORDINATE)
               .addExecution(
                        ExecutionBuilder
                                 .create()
                                 .setId("create-forge-addon")
                                 .setPhase("package")
                                 .addGoal("jar")
                                 .setConfig(ConfigurationBuilder.create().addConfigurationElement(
                                          ConfigurationElementBuilder.create().setName("classifier")
                                                   .setText("forge-addon"))));
      pluginFacet.addPlugin(plugin);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      boolean installed = false;
      if (getFaceted().hasFacet(MavenPluginFacet.class)
               && getFaceted().getFacet(MavenPluginFacet.class).hasPlugin(JAR_PLUGIN_COORDINATE))
      {
         List<Execution> executions = getFaceted().getFacet(MavenPluginFacet.class).getPlugin(JAR_PLUGIN_COORDINATE)
                  .listExecutions();
         for (Execution execution : executions)
         {
            if ("create-forge-addon".equals(execution.getId())
                     && "forge-addon".equals(execution.getConfig().getConfigurationElement("classifier").getText()))
            {
               installed = true;
               break;
            }
         }
      }
      return installed;
   }
}
