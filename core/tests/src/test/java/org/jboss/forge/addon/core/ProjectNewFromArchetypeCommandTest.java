package org.jboss.forge.addon.core;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectNewFromArchetypeCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
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
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private UITestHarness testHarness;

   @Test
   public void testArchetypeWizard() throws Exception
   {
      try
      {
         WizardCommandController controller = testHarness.createWizardController("Project: New");
         controller.initialize();
         controller.setValueFor("named", "example");
         Assert.assertFalse(controller.canMoveToNextStep());
         controller.setValueFor("type", "From Archetype");
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("archetypeGroupId", "org.jboss.tools.archetypes");
         controller.setValueFor("archetypeArtifactId", "jboss-forge-html5");
         controller.setValueFor("archetypeVersion", "1.0.0-SNAPSHOT");
         Assert.assertTrue(controller.canExecute());
      }
      finally
      {

      }
   }
}