/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.forge.addon.scaffold.MockScaffoldProvider.PROVIDER_DESCRIPTION;
import static org.jboss.forge.addon.scaffold.MockScaffoldProvider.PROVIDER_NAME;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ScaffoldAddonTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:scaffold"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(MockScaffoldProvider.class, ProjectHelper.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:scaffold"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private ProjectHelper projectHelper;

   @Test
   public void testCanLoadScaffoldProviders() throws Exception
   {
      Imported<ScaffoldProvider> providerInstances = registry.getServices(ScaffoldProvider.class);
      List<ScaffoldProvider> providerList = new ArrayList<ScaffoldProvider>();
      for (ScaffoldProvider provider : providerInstances)
      {
         providerList.add(provider);
      }
      assertFalse(providerList.isEmpty());
      assertNotNull(providerList.get(0));
      ScaffoldProvider scaffoldProvider = providerInstances.get();
      assertEquals(PROVIDER_NAME, scaffoldProvider.getName());
      assertEquals(PROVIDER_DESCRIPTION, scaffoldProvider.getDescription());
   }

   @Test
   public void testCanSetupScaffoldProvider() throws Exception
   {
      Imported<ScaffoldProvider> providerInstances = registry.getServices(ScaffoldProvider.class);
      ScaffoldProvider scaffoldProvider = providerInstances.get();
      assertNotNull(scaffoldProvider);
      Project project = projectHelper.createWebProject();
      ScaffoldSetupContext setupContext = new ScaffoldSetupContext("", true);
      scaffoldProvider.setup(project, setupContext);
      assertTrue(scaffoldProvider.isSetup(setupContext));
   }
}