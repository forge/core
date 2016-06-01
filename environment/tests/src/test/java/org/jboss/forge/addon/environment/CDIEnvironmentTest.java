/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.environment;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIEnvironmentTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:environment"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(TestCategory.class, UserInterfaceCategory.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:environment")
               );
      return archive;
   }

   @Inject
   private Environment environment;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(environment);
   }

   @Test
   public void testGetCategory() throws Exception
   {
      Map<Object, Object> mapTest = environment.get(TestCategory.class);
      Map<Object, Object> mapUI = environment.get(UserInterfaceCategory.class);

      Assert.assertNotNull(mapTest);
      Assert.assertNotNull(mapUI);

      Assert.assertNotSame(mapTest, mapUI);

      mapTest.put("Key", "Value");
      Assert.assertFalse(mapTest.isEmpty());

      Map<Object, Object> newMap = environment.get(TestCategory.class);

      Assert.assertSame(mapTest, newMap);
      Assert.assertEquals("Value", newMap.get("Key"));
   }
}
