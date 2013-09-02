/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.wizard.subflow.ExampleFlow;
import org.jboss.forge.addon.shell.mock.wizard.subflow.FlowOneOneStep;
import org.jboss.forge.addon.shell.mock.wizard.subflow.FlowOneStep;
import org.jboss.forge.addon.shell.mock.wizard.subflow.FlowTwoStep;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class WizardSubFlowCompletionTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(ExampleFlow.class, FlowOneStep.class, FlowOneOneStep.class, FlowTwoStep.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private ShellTest test;

   @After
   public void after() throws IOException
   {
      test.clearScreen();
   }

   @Test
   public void testWizardInitialStepAutocomplete() throws Exception
   {
      // flow --name george --flowOneInput xyz --flowOneOneInput abc --flowTwoInput 42
      int timeoutQuantity = 5;
      test.waitForCompletion("flow ", "fl", timeoutQuantity, TimeUnit.SECONDS);
      test.waitForCompletion("flow --name ", "--", timeoutQuantity, TimeUnit.SECONDS);
      test.waitForCompletion("flow --name george --flowOneInput ", "george --", timeoutQuantity, TimeUnit.SECONDS);
      test.waitForCompletion("flow --name george --flowOneInput xyz --flowOneOneInput ", "xyz --", timeoutQuantity,
               TimeUnit.SECONDS);
      test.waitForCompletion("flow --name george --flowOneInput xyz --flowOneOneInput abc --flowTwoInput ", "abc --",
               timeoutQuantity, TimeUnit.SECONDS);
   }
}
