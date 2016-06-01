/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class PromptCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-example"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-example"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   UITestHarness testHarness;

   @Test
   public void testDefaultPromptValue() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("prompt"))
      {
         controller.initialize();
         Result result = controller.execute();
         Assert.assertEquals("You answered: null", result.getMessage());
      }
   }

   @Test
   public void testOverridenPromptValue() throws Exception
   {
      testHarness.getPromptResults().put("Type something: ", "Something");
      try (CommandController controller = testHarness.createCommandController("prompt"))
      {
         controller.initialize();
         Result result = controller.execute();
         Assert.assertEquals("You answered: Something", result.getMessage());
      }
   }

   @Test
   public void testDefaultBooleanPromptValue() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("prompt-boolean"))
      {
         controller.initialize();
         Result result = controller.execute();
         Assert.assertEquals("You answered: true", result.getMessage());
      }
   }

   @Test
   public void testOverridenBooleanPromptValue() throws Exception
   {
      testHarness.getPromptResults().put("Do you love Forge 2\\?", "false");
      try (CommandController controller = testHarness.createCommandController("prompt-boolean"))
      {
         controller.initialize();
         Result result = controller.execute();
         Assert.assertEquals("You answered: false", result.getMessage());
      }
   }

}
