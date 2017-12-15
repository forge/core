/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.mock.MethodCountCommand;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CacheCommandControllerTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:assertj"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackage(MockUIRuntime.class.getPackage())
               .addClasses(MethodCountCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:assertj"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Test
   public void isEnabledCallsAreCachedUntilSetValueIsCalled() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(MethodCountCommand.class))
      {
         controller.initialize();
         Map<String, AtomicInteger> counts = ((MethodCountCommand) controller.getCommand()).getCounts();

         assertThat(controller.isEnabled()).isTrue();
         assertThat(controller.isEnabled()).isTrue();
         assertThat(counts.getOrDefault("isEnabled", new AtomicInteger()).intValue()).isEqualTo(1)
                  .withFailMessage("isEnabled()");

         controller.setValueFor("firstName", "George");

         assertThat(controller.isEnabled()).isTrue();
         assertThat(controller.isEnabled()).isTrue();
         assertThat(counts.getOrDefault("isEnabled", new AtomicInteger()).intValue()).isEqualTo(2)
                  .withFailMessage("isEnabled()");
      }
   }

   @Test
   public void canExecuteCallsAreCachedUntilSetValueIsCalled() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(MethodCountCommand.class))
      {
         controller.initialize();
         Map<String, AtomicInteger> counts = ((MethodCountCommand) controller.getCommand()).getCounts();

         assertThat(controller.canExecute()).isTrue();
         assertThat(controller.canExecute()).isTrue();
         assertThat(counts.getOrDefault("validate", new AtomicInteger()).intValue()).isEqualTo(1)
                  .withFailMessage("validate()");

         controller.setValueFor("firstName", "George");

         assertThat(controller.canExecute()).isTrue();
         assertThat(controller.canExecute()).isTrue();
         assertThat(counts.getOrDefault("validate", new AtomicInteger()).intValue()).isEqualTo(2)
                  .withFailMessage("validate()");
      }
   }

}
