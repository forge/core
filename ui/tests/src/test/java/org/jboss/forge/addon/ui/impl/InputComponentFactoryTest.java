/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InputComponentFactoryTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.furnace:container-cdi", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace:container-cdi", "2.0.0-SNAPSHOT"));

      return archive;
   }

   @Inject
   InputComponentFactory factory;

   @Test
   public void testCreateUIInput() throws Exception
   {
      UIInput<String> input = factory.createInput("foo", String.class);
      Assert.assertNotNull(input);
   }

   @Test
   public void testCreateUIInputMany() throws Exception
   {
      UIInputMany<String> input = factory.createInputMany("foo", String.class);
      Assert.assertNotNull(input);
   }

   @Test
   public void testCreateUISelectMany() throws Exception
   {
      UISelectMany<String> input = factory.createSelectMany("foo", String.class);
      Assert.assertNotNull(input);
   }

   @Test
   public void testCreateUISelectOne() throws Exception
   {
      UISelectOne<String> input = factory.createSelectOne("foo", String.class);
      Assert.assertNotNull(input);
   }
}
