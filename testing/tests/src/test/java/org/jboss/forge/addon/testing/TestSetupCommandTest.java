/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.testing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.testing.facet.JUnitTestingFacet;
import org.jboss.forge.addon.testing.facet.TestNGTestingFacet;
import org.jboss.forge.addon.testing.ui.TestSetupCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.jboss.forge.addon.testing.facet.JUnitTestingFacet.*;
import static org.jboss.forge.addon.testing.facet.TestNGTestingFacet.TEST_NG_ARTIFACT_ID;
import static org.jboss.forge.addon.testing.facet.TestNGTestingFacet.TEST_NG_FRAMEWORK_NAME;
import static org.jboss.forge.addon.testing.facet.TestNGTestingFacet.TEST_NG_GROUP_ID;
import static org.jboss.forge.addon.testing.facet.TestNGTestingFacet.TEST_NG_SCOPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class TestSetupCommandTest
{

   private Project project;

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addAsServiceProvider(Service.class, TestSetupCommandTest.class);
   }

   private UITestHarness testHarness;

   private ShellTest shellTest;

   @Before
   public void setUp() throws Exception
   {
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(),
               ProjectFactory.class).get();
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
      shellTest = SimpleContainer.getServices(getClass().getClassLoader(), ShellTest.class).get();
      project = projectFactory.createTempProject();
   }

   @Test
   public void testCommandMetadata() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController(TestSetupCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         assertTrue(controller.getCommand() instanceof TestSetupCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("Testing: Setup", metadata.getName());
         assertEquals("Testing", metadata.getCategory().getName());
         assertTrue(controller.hasInput("testFramework"));
         assertTrue(controller.hasInput("version"));
      }
   }

   @Test
   public void testSetupTestNGViaUI() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController(TestSetupCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         final String version = "6.9.4";

         controller.setValueFor("testFramework", TEST_NG_FRAMEWORK_NAME);
         controller.setValueFor("version", version);
         final Result result = controller.execute();
         assertFalse(result instanceof Failed);
         assertTrue(project.hasFacet(TestNGTestingFacet.class));
         DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
         assertTrue(dependencyFacet.hasDirectDependency(DependencyBuilder
                  .create(TEST_NG_GROUP_ID + ":" + TEST_NG_ARTIFACT_ID + ":" + version + ":" + TEST_NG_SCOPE)));
      }
   }

   @Test
   public void testSetupJUnitViaShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      final String version = "4.1.12";
      Result result = shellTest
               .execute("testing-setup --test-framework junit --version " + version, 2, TimeUnit.SECONDS);
      assertFalse(result instanceof Failed);
      assertTrue(project.hasFacet(JUnitTestingFacet.class));
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      assertTrue(dependencyFacet.hasDirectDependency(DependencyBuilder.create(
               JUNIT_GROUP_ID + ":" + JUNIT_ARTIFACT_ID + ":" + version + ":" + JUNIT_SCOPE)));
   }

   @After
   public void tearDown()
   {
      project.getRoot().delete(true);
   }
}
