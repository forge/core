/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import java.util.Iterator;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ArchetypeRegistryTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(TestArchetypeCatalogFactory.class)
               .addAsServiceProvider(Service.class, ArchetypeRegistryTest.class, TestArchetypeCatalogFactory.class);

      return archive;
   }

   private ArchetypeCatalogFactoryRegistry archetypeRegistry;

   @Before
   public void setUp()
   {
      archetypeRegistry = SimpleContainer
               .getServices(getClass().getClassLoader(), ArchetypeCatalogFactoryRegistry.class).get();
   }

   @Test
   public void testArchetypeCatalogFactory()
   {
      ArchetypeCatalogFactory archetypeCatalogFactory = archetypeRegistry
               .getArchetypeCatalogFactory("Test");
      Assert.assertNotNull(archetypeCatalogFactory);
      ArchetypeCatalog archetypes = archetypeCatalogFactory.getArchetypeCatalog();
      Assert.assertNotNull(archetypes);
      Assert.assertNotNull(archetypes.getArchetypes());
      Assert.assertEquals(1, archetypes.getArchetypes().size());
      Archetype expected = new Archetype();
      expected.setGroupId("groupId");
      expected.setArtifactId("artifactId");
      expected.setVersion("1.0.0");
      expected.setDescription("Description");
      Assert.assertEquals(expected, archetypes.getArchetypes().get(0));
   }

   @Test
   public void testHasArchetypeCatalogFactory()
   {
      Assert.assertTrue(archetypeRegistry.hasArchetypeCatalogFactories());
   }

   @Test
   public void testDuplicateArchetypeCatalogs()
   {
      TestArchetypeCatalogFactory factory = new TestArchetypeCatalogFactory();
      archetypeRegistry.addArchetypeCatalogFactory(factory);
      Iterator<ArchetypeCatalogFactory> archetypeCatalogFactories = archetypeRegistry.getArchetypeCatalogFactories()
               .iterator();
      Assert.assertTrue(archetypeCatalogFactories.hasNext());
      Assert.assertSame(factory, archetypeCatalogFactories.next());
      Assert.assertFalse(archetypeCatalogFactories.hasNext());
   }
}
