/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenPluginFacetPluginDependenciesTest extends AbstractShellTest
{
   @Test
   public void testPluginDependencyRetrieval() throws Exception
   {
      // Setup
      Project project = initializeJavaProject();
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      Dependency pluginArtifact = DependencyBuilder.create("maven-compiler-plugin");
      Dependency dependency = DependencyBuilder.create("org.jboss.errai:errai-common:2.4.3.Final");
      // Add plugin
      pluginFacet.addPlugin(MavenPluginBuilder.create().setDependency(DependencyBuilder.create(pluginArtifact))
               .addPluginDependency(dependency));

      // This will fail
      Assert.assertFalse(pluginFacet.getPlugin(pluginArtifact).getDirectDependencies().isEmpty());
   }
}
