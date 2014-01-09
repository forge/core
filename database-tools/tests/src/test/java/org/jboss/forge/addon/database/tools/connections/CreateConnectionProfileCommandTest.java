/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.connections;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.UITestHarness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CreateConnectionProfileCommandTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:configuration"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:database-tools"),
            @AddonDependency(name = "org.jboss.forge.addon:dependencies"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:database-tools"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:dependencies"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"))
               .addClass(MockConnectionProfileManagerImpl.class);
      return archive;
   }

   @Inject
   private ConnectionProfileManager manager;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private DependencyResolver resolver;

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