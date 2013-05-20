/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.addons;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.furnace.addons.AddonDependency;
import org.jboss.forge.furnace.addons.AddonDependencyImpl;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonTree;
import org.jboss.forge.furnace.impl.AddonImpl;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.mock.MockLockManager;
import org.jboss.forge.furnace.mock.MockMarkAddonDirtyVisitor;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.SingleVersionRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonTreeTest
{
   private LockManager lock = new MockLockManager();

   private SingleVersion version = new SingleVersion("1");
   private SingleVersionRange versionRange = new SingleVersionRange(version);
   private AddonImpl addonA = null;
   private AddonImpl addonB = null;
   private AddonImpl addonC = null;
   private AddonImpl addonD = null;

   @Before
   public void before()
   {
      addonA = new AddonImpl(lock, AddonId.from("A", "1"));
      addonA.setDirty(false);
      addonB = new AddonImpl(lock, AddonId.from("B", "1"));
      addonB.setDirty(false);
      addonC = new AddonImpl(lock, AddonId.from("C", "1"));
      addonC.setDirty(false);
      addonD = new AddonImpl(lock, AddonId.from("D", "1"));
      addonD.setDirty(false);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCannotAddDuplicateNodes()
   {
      Set<AddonDependency> dependencies = new HashSet<AddonDependency>();
      dependencies.add(new AddonDependencyImpl(lock, addonA, versionRange, addonB, false, false));
      addonA.setDependencies(dependencies);

      AddonTree tree = new AddonTree(lock);
      tree.add(addonA);
      tree.add(addonB);
   }

   @Test
   public void testMarkDirtyVisitor()
   {
      Set<AddonDependency> dependencies = new HashSet<AddonDependency>();
      dependencies.add(new AddonDependencyImpl(lock, addonA, new SingleVersionRange(version), addonB, false, false));
      addonA.setDependencies(dependencies);

      AddonTree tree = new AddonTree(lock);
      tree.add(addonB);
      tree.add(addonA);
      tree.add(addonC);
      tree.add(addonD);

      Assert.assertFalse(addonA.isDirty());
      Assert.assertFalse(addonB.isDirty());
      Assert.assertFalse(addonC.isDirty());
      Assert.assertFalse(addonD.isDirty());

      tree.depthFirst(new MockMarkAddonDirtyVisitor(tree, addonB));

      Assert.assertTrue(addonA.isDirty());
      Assert.assertFalse(addonB.isDirty());
      Assert.assertFalse(addonC.isDirty());
      Assert.assertFalse(addonD.isDirty());
   }

   @Test
   public void testMarkDirtyVisitor2()
   {
      Set<AddonDependency> dependencies = new HashSet<AddonDependency>();
      dependencies.add(new AddonDependencyImpl(lock, addonA, new SingleVersionRange(version), addonB, false, false));
      addonA.setDependencies(dependencies);

      dependencies = new HashSet<AddonDependency>();
      dependencies.add(new AddonDependencyImpl(lock, addonB, new SingleVersionRange(version), addonC, false, false));
      dependencies.add(new AddonDependencyImpl(lock, addonB, new SingleVersionRange(version), addonD, true, false));
      addonB.setDependencies(dependencies);

      dependencies = new HashSet<AddonDependency>();
      dependencies.add(new AddonDependencyImpl(lock, addonC, new SingleVersionRange(version), addonD, true, false));
      addonC.setDependencies(dependencies);

      AddonTree tree = new AddonTree(lock);
      tree.add(addonA);

      Assert.assertTrue(tree.contains(addonA));
      Assert.assertTrue(tree.contains(addonB));
      Assert.assertTrue(tree.contains(addonC));
      Assert.assertTrue(tree.contains(addonD));

      Assert.assertFalse(addonA.isDirty());
      Assert.assertFalse(addonB.isDirty());
      Assert.assertFalse(addonC.isDirty());
      Assert.assertFalse(addonD.isDirty());

      tree.depthFirst(new MockMarkAddonDirtyVisitor(tree, addonD));

      Assert.assertTrue(addonA.isDirty());
      Assert.assertTrue(addonB.isDirty());
      Assert.assertTrue(addonC.isDirty());
      Assert.assertFalse(addonD.isDirty());
   }

}
