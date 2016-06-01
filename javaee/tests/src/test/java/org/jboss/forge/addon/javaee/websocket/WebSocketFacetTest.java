/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.addon.javaee.facets.JavaEE7Facet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebSocketFacetTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private DependencyInstaller dependencyInstaller;

   @Test(expected = FacetIsAmbiguousException.class)
   public void testCannotInstallAmbiguousFacetType() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, WebSocketFacet.class);
   }

   @Test
   public void testWebSocket_1_0() throws Exception
   {
      Project project = projectFactory.createTempProject();
      WebSocketFacet_1_0 facet = facetFactory.install(project, WebSocketFacet_1_0.class);
      Assert.assertNotNull(facet);
      Assert.assertTrue(dependencyInstaller.isInstalled(project,
               DependencyBuilder.create("javax.websocket:javax.websocket-api:1.0")
                        .setScopeType("provided")));
   }

   @Test
   public void testJavaEE7FacetContainsWebSocket_1_0_() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaEE7Facet.class);
      WebSocketFacet_1_0 facet = facetFactory.install(project, WebSocketFacet_1_0.class);
      Assert.assertNotNull(facet);
      Assert.assertFalse(dependencyInstaller.isInstalled(project,
               DependencyBuilder.create("javax.websocket:javax.websocket-api:1.0")
                        .setScopeType("provided")));
   }

   @Test
   public void testWebSocket_1_1() throws Exception
   {
      Project project = projectFactory.createTempProject();
      WebSocketFacet_1_1 facet = facetFactory.install(project, WebSocketFacet_1_1.class);
      Assert.assertNotNull(facet);
      Assert.assertTrue(dependencyInstaller.isInstalled(project,
               DependencyBuilder.create("javax.websocket:javax.websocket-api:1.1")
                        .setScopeType("provided")));
   }

}