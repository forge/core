/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.maven.plugins.Configuration;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceMetaModelFacetTest
{

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testInstallFacet() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      projectHelper.installJPA_2_0(project);
      facetFactory.install(project, PersistenceMetaModelFacet.class);
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPlugin processorPlugin = facet.getPlugin(CoordinateBuilder.create("org.bsc.maven:maven-processor-plugin"));
      MavenPlugin compilerPlugin = facet.getPlugin(CoordinateBuilder
               .create("org.apache.maven.plugins:maven-compiler-plugin"));

      assertTrue(project.hasFacet(PersistenceMetaModelFacet.class));
      assertNotNull(processorPlugin);
      assertEquals(1, processorPlugin.listExecutions().size());
      Configuration processorConfig = processorPlugin.listExecutions().get(0).getConfig();
      assertEquals("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor",
               processorConfig.getConfigurationElement("processors").getChildByName("processor").getText());
      Configuration compilerConfig = compilerPlugin.getConfig();
      assertEquals("none", compilerConfig.getConfigurationElement("proc").getText());
   }
}
