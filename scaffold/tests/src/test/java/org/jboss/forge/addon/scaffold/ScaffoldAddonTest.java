/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.scaffold.mock.MockProvider;
import org.jboss.forge.addon.scaffold.mock.Scaffoldable;
import org.jboss.forge.addon.scaffold.mock.ScaffoldedResource;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ScaffoldAddonTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:scaffold"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackage(MockProvider.class.getPackage())
               .addClass(ProjectHelper.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:scaffold"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private ResourceFactory resourceFactory;

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