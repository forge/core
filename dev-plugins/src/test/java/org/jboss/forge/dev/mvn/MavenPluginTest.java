/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.mvn;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class MavenPluginTest extends AbstractShellTest
{

   @Test
   public void testAddPlugin() throws Exception
   {
      Project project = initializeJavaProject();
      String pluginCoords = "org.jboss.forge:forge-maven-plugin:1.0.0.Final";
      getShell().execute("maven add-plugin " + pluginCoords);
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = facet.getPlugin(DependencyBuilder.create(pluginCoords));
      Assert.assertNotNull(plugin);
   }

   @Test
   public void testAddManagedPlugin() throws Exception
   {
      Project project = initializeJavaProject();
      String pluginCoords = "org.jboss.forge:forge-maven-plugin:1.0.0.Final";
      getShell().execute("maven add-managed-plugin " + pluginCoords);
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = facet.getManagedPlugin(DependencyBuilder.create(pluginCoords));
      Assert.assertNotNull(plugin);
   }
}
