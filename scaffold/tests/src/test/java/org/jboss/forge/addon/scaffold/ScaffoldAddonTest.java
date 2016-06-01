/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold;

import static org.jboss.forge.addon.scaffold.mock.MockProvider.PROVIDER_DESCRIPTION;
import static org.jboss.forge.addon.scaffold.mock.MockProvider.PROVIDER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.scaffold.mock.MockProvider;
import org.jboss.forge.addon.scaffold.mock.Scaffoldable;
import org.jboss.forge.addon.scaffold.mock.ScaffoldableResourceGenerator;
import org.jboss.forge.addon.scaffold.mock.ScaffoldedResource;
import org.jboss.forge.addon.scaffold.mock.ScaffoldedResourceGenerator;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ScaffoldAddonTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:scaffold"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackage(MockProvider.class.getPackage())
               .addClass(ProjectHelper.class)
               .addAsServiceProvider(Service.class, ScaffoldAddonTest.class, ScaffoldableResourceGenerator.class,
                        ScaffoldedResourceGenerator.class, ProjectHelper.class, MockProvider.class);
      return archive;
   }

   private AddonRegistry registry;
   private ProjectHelper projectHelper;
   private ResourceFactory resourceFactory;

   @Before
   public void setUp()
   {
      registry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      projectHelper = SimpleContainer.getServices(getClass().getClassLoader(), ProjectHelper.class).get();
      resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testCanLoadScaffoldProviders() throws Exception
   {
      // Setup

      // Execute
      Imported<ScaffoldProvider> providerInstances = registry.getServices(ScaffoldProvider.class);
      ScaffoldProvider scaffoldProvider = providerInstances.get();

      // Verify
      assertNotNull(scaffoldProvider);
      assertEquals(PROVIDER_NAME, scaffoldProvider.getName());
      assertEquals(PROVIDER_DESCRIPTION, scaffoldProvider.getDescription());
   }

   @Test
   public void testCanSetupScaffoldProvider() throws Exception
   {
      // Setup
      ScaffoldProvider scaffoldProvider = getScaffoldProvider();
      Project project = projectHelper.createWebProject();
      ScaffoldSetupContext setupContext = new ScaffoldSetupContext("", project);

      // Execute
      scaffoldProvider.setup(setupContext);

      // Verify
      assertTrue(scaffoldProvider.isSetup(setupContext));
   }

   @Test
   public void testCanGenerateScaffold() throws Exception
   {
      // Setup
      ScaffoldProvider scaffoldProvider = getScaffoldProvider();
      Project project = projectHelper.createWebProject();
      ScaffoldSetupContext setupContext = new ScaffoldSetupContext("", project);
      scaffoldProvider.setup(setupContext);

      // Execute
      List<Resource<?>> scaffoldables = new ArrayList<Resource<?>>();
      scaffoldables.add(resourceFactory.create(new Scaffoldable("mock")));
      ScaffoldGenerationContext generationContext = new ScaffoldGenerationContext("", scaffoldables, project);
      List<Resource<?>> generatedResources = scaffoldProvider.generateFrom(generationContext);

      // Verify
      assertTrue(generatedResources.size() > 0);
      assertTrue(generatedResources.get(0) instanceof ScaffoldedResource);
   }

   private ScaffoldProvider getScaffoldProvider()
   {
      Imported<ScaffoldProvider> providerInstances = registry.getServices(ScaffoldProvider.class);
      ScaffoldProvider scaffoldProvider = providerInstances.get();
      assertNotNull(scaffoldProvider);
      return scaffoldProvider;
   }
}