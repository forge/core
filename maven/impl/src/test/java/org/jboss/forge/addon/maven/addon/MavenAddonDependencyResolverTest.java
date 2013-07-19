/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.addon;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.spi.AddonInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MavenAddonDependencyResolverTest extends AbstractTestClass
{
   private MavenAddonDependencyResolver resolver;

   @Before
   public void setUp() throws IOException
   {
      resolver = new MavenAddonDependencyResolver();
   }

   @Test
   public void testResolutionInfo() throws Exception
   {
      AddonId addon = AddonId.from("test:one_dep", "1.0.0.Final");
      AddonId addonDep = AddonId.from("test:no_dep", "1.0.0.Final");
      AddonInfo info = resolver.resolveAddonDependencyHierarchy(addon);
      Assert.assertNotNull(info);
      Assert.assertEquals(1, info.getRequiredAddons().size());
      Assert.assertEquals(addonDep, info.getRequiredAddons().iterator().next().getAddon());
      Assert.assertEquals(1, info.getResources().size());
   }

   @Test
   public void testIndirectResolutionInfo() throws Exception
   {
      AddonId addon = AddonId.from("test:indirect_dep", "1.0.0.Final");
      AddonInfo info = resolver.resolveAddonDependencyHierarchy(addon);
      Assert.assertNotNull(info);
      Set<AddonId> requiredAddons = new HashSet<AddonId>();
      for (AddonInfo ai : info.getRequiredAddons())
      {
         requiredAddons.add(ai.getAddon());
      }

      AddonId[] expecteds = new AddonId[] {
               AddonId.from("test:one_dep", "1.0.0.Final")
      };
      Assert.assertEquals(expecteds.length, requiredAddons.size());
      Assert.assertThat(requiredAddons, CoreMatchers.hasItems(expecteds));
   }

   /**
    * A->B->C <br/>
    * A->D->C
    * 
    * @throws Exception
    */
   @Test
   public void testResolutionTwoDependencies() throws Exception
   {
      AddonId addon = AddonId.from("test:two_deps", "1.0.0.Final");
      AddonInfo info = resolver.resolveAddonDependencyHierarchy(addon);
      Assert.assertNotNull(info);
      Set<AddonId> requiredAddons = new HashSet<AddonId>();
      for (AddonInfo ai : info.getRequiredAddons())
      {
         requiredAddons.add(ai.getAddon());
      }
      AddonId[] expecteds = new AddonId[] {
               AddonId.from("test:one_dep_a", "1.0.0.Final"),
               AddonId.from("test:one_dep", "1.0.0.Final")
      };
      Assert.assertEquals(expecteds.length, requiredAddons.size());
      Assert.assertThat(requiredAddons, CoreMatchers.hasItems(expecteds));
   }

   @Test
   public void testResolutionInfoLib() throws Exception
   {
      AddonId addon = AddonId.from("test:one_dep_lib", "1.0.0.Final");
      AddonInfo info = resolver.resolveAddonDependencyHierarchy(addon);
      Assert.assertNotNull(info);
      Assert.assertTrue(info.getRequiredAddons().isEmpty());
      Assert.assertEquals(2, info.getResources().size());
   }
}
