/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import static org.hamcrest.CoreMatchers.hasItems;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockDisabledProjectType;
import org.jboss.forge.addon.projects.mock.MockMetadataFacet;
import org.jboss.forge.addon.projects.mock.MockProject;
import org.jboss.forge.addon.projects.mock.MockProjectType;
import org.jboss.forge.addon.projects.mock.MockStacksDisabledProjectType;
import org.jboss.forge.addon.projects.ui.NewProjectWizard;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(MockProjectType.class)
               .addClass(MockDisabledProjectType.class)
               .addClass(MockBuildSystem.class)
               .addClass(MockStacksDisabledProjectType.class)
               .addClass(MockProject.class)
               .addClass(MockMetadataFacet.class)
               .addAsServiceProvider(Service.class, NewProjectWizardTest.class,
                        MockProjectType.class,
                        MockStacksDisabledProjectType.class,
                        MockDisabledProjectType.class,
                        MockBuildSystem.class,
                        MockMetadataFacet.class);

      return archive;
   }

   private UITestHarness testHarness;

   @Before
   public void setUp()
   {
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
   }

   @Test
   public void testInvokeCommand() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         Assert.assertTrue(wizard.isValid());
         Assert.assertTrue(wizard.canExecute());
         File targetDirectory = new File(tempDir, "test");
         Assert.assertFalse(targetDirectory.exists());
         wizard.execute();

         Assert.assertTrue(targetDirectory.exists());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testTargetDirHasDefaultValue() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());

         Resource<?> targetLocation = (Resource<?>) wizard.getValueFor("targetLocation");
         Assert.assertNotNull(targetLocation);

         wizard.setValueFor("named", "test");
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         Assert.assertTrue(wizard.isValid());
         Assert.assertTrue(wizard.canExecute());

         wizard.execute();
         Assert.assertTrue(targetLocation.exists());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testValidateProjectName() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "Test Project Name Invalid");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");

         Assert.assertFalse(wizard.isValid());
         Assert.assertFalse(wizard.canExecute());

         wizard.setValueFor("named", "Test-Project-Name-Valid");

         Assert.assertTrue(wizard.isValid());
         Assert.assertTrue(wizard.canExecute());

         File targetDirectory = new File(tempDir, "Test-Project-Name-Valid");
         Assert.assertFalse(targetDirectory.exists());
         Result result = wizard.execute();
         Assert.assertThat(result, not(instanceOf(Failed.class)));

         Assert.assertTrue(targetDirectory.exists());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testOverwriteEnabledWhenTargetDirectoryExistsNotEmpty() throws Exception
   {

      File tempDir = OperatingSystemUtils.createTempDir();
      File something = new File(tempDir, "test/something");
      something.mkdirs();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         Assert.assertFalse(wizard.getInputs().get("overwrite").isEnabled());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         Assert.assertFalse(wizard.isValid());
         Assert.assertTrue(wizard.getInputs().get("overwrite").isEnabled());
         Assert.assertFalse(wizard.canMoveToNextStep());
         Assert.assertFalse(wizard.canExecute());
      }
      finally
      {
         something.delete();
         tempDir.delete();
      }
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDisabledProjectTypeIsNotListed() throws Exception
   {
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Map<String, InputComponent<?, ?>> inputs = wizard.getInputs();
         Assert.assertThat(inputs.get("type"), instanceOf(UISelectOne.class));
         UISelectOne<ProjectType> projectTypes = (UISelectOne<ProjectType>) inputs.get("type");
         List<ProjectType> list = Lists.toList(projectTypes.getValueChoices());
         Assert.assertEquals(2, list.size());
         Set<String> typeNames = list.stream().map(type -> type.getType()).collect(Collectors.toSet());
         Assert.assertThat(typeNames, hasItems("mock", "mock-stacks-disabled"));
      }
   }

   @Test
   public void testStackIsDisabledForUnsupportedProjectType() throws Exception
   {
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Map<String, InputComponent<?, ?>> inputs = wizard.getInputs();
         wizard.setValueFor("type", "mock-stacks-disabled");
         Assert.assertThat(wizard.getValueFor("type"), instanceOf(MockStacksDisabledProjectType.class));
         Assert.assertThat(inputs.get("stack").isEnabled(), is(false));
      }
   }

   @Test
   public void testOverwriteDisabledWhenUseTargetDirectoryRoot() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File something = new File(tempDir, "test/something");
      something.mkdirs();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         Assert.assertFalse(wizard.getInputs().get("overwrite").isEnabled());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", something);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         wizard.setValueFor("useTargetLocationRoot", "true");
         Assert.assertFalse(wizard.getInput("overwrite").isEnabled());
         Assert.assertTrue(wizard.canExecute());
         Result result = wizard.execute();
         Assert.assertThat(result, not(instanceOf(Failed.class)));
      }
      finally
      {
         something.delete();
         tempDir.delete();
      }
   }

}
