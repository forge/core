/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import java.util.ArrayList;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.database.tools.connections.ui.CreateConnectionProfileCommand;
import org.jboss.forge.addon.database.tools.jpa.HibernateDialect;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CreateConnectionProfileCommandTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:configuration"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:database-tools"),
            @AddonDeployment(name = "org.jboss.forge.addon:dependencies"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(MockConnectionProfileManagerImpl.class)
               .addAsServiceProvider(Service.class, CreateConnectionProfileCommandTest.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:simple"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:database-tools"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:dependencies"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"));
      return archive;
   }

   private ConnectionProfileManagerProvider provider;
   private ConnectionProfileManager manager;
   private UITestHarness testHarness;
   private DependencyResolver resolver;

   @Before
   public void setUp()
   {
      provider = SimpleContainer.getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
      manager = new MockConnectionProfileManagerImpl();
      provider.setConnectionProfileManager(manager);
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
      resolver = SimpleContainer.getServices(getClass().getClassLoader(), DependencyResolver.class).get();
   }

   @Test
   public void testConnectionProfileManager() throws Exception
   {
      Assert.assertNotNull(manager);
      Map<String, ConnectionProfile> profiles = manager.loadConnectionProfiles();
      Assert.assertNotNull(profiles);
      Assert.assertEquals(1, profiles.size());
      manager.saveConnectionProfiles(new ArrayList<ConnectionProfile>());
      profiles = manager.loadConnectionProfiles();
      Assert.assertEquals(0, profiles.size());
   }

   @Test
   public void testCreateConnectionProfileCommand() throws Exception
   {
      CommandController command = testHarness.createCommandController(CreateConnectionProfileCommand.class);
      command.initialize();
      command.setValueFor("name", "test");
      command.setValueFor("jdbcUrl", "jdbc:h2:~/app-root/data/sakila");
      command.setValueFor("userName", "sa");
      command.setValueFor("userPassword", "");
      command.setValueFor("hibernateDialect", HibernateDialect.fromClassName("org.hibernate.dialect.H2Dialect"));
      command.setValueFor("driverLocation", resolveH2DriverJarResource());
      command.setValueFor("driverClass", "org.h2.Driver");
      command.execute();
      Map<String, ConnectionProfile> profiles = manager.loadConnectionProfiles();
      Assert.assertEquals(1, profiles.size());
      ConnectionProfile profile = profiles.get("test");
      Assert.assertNotNull(profile);
      Assert.assertEquals("org.h2.Driver", profile.getDriver());
   }

   private FileResource<?> resolveH2DriverJarResource()
   {
      DependencyQuery query = DependencyQueryBuilder.create("com.h2database:h2:1.3.167");
      Dependency dependency = resolver.resolveArtifact(query);
      if (dependency != null)
      {
         return dependency.getArtifact();
      }
      else
      {
         return null;
      }
   }
}