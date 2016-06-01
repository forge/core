/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ResourceFactoryGeneratorTest
{
   @Deployment(order = 1)
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:resources") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, ResourceFactoryGeneratorTest.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:simple"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("mockstring", "1"));

      return archive;
   }

   @Deployment(testable = false, name = "mockstring,1", order = 3)
   public static AddonArchive getAddonDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(MockStringResource.class, MockStringResourceGenerator.class)
               .addAsServiceProvider(Service.class, MockStringResourceGenerator.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:simple"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"));

      return archive;
   }

   private ResourceFactory factory;

   @Before
   public void setUp()
   {
      this.factory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testCreateResourceFromAddon() throws Exception
   {
      Assert.assertNotNull(factory);
      MockStringResource resource = (MockStringResource) factory.create("It's a valid string!");
      Assert.assertNotNull(resource);
      Assert.assertEquals("It's a valid string!", resource.getUnderlyingResourceObject());
   }

   @Test
   public void testCreateUnhandledResourceFromAddon() throws Exception
   {
      Assert.assertNotNull(factory);
      MockStringResource resource = (MockStringResource) factory.create("It's a bad string!");
      Assert.assertNull(resource);
   }

}