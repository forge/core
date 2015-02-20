/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.Table;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.ui.NewEntityCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link NewEntityCommand} behavior
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewEntityCommandShellTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClass(ProjectHelper.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private ShellTest test;

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installJPA_2_0(project);
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      CommandController controller = uiTestHarness.createCommandController(NewEntityCommand.class, project.getRoot());
      controller.initialize();
      // Checks the command metadata
      assertTrue(controller.getCommand() instanceof NewEntityCommand);
      UICommandMetadata metadata = controller.getMetadata();
      assertEquals("JPA: New Entity", metadata.getName());
      assertEquals("Java EE", metadata.getCategory().getName());
      assertEquals("JPA", metadata.getCategory().getSubCategory().getName());
      assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
      assertEquals(4, controller.getInputs().size());
      assertTrue(controller.hasInput("named"));
      assertTrue(controller.hasInput("targetPackage"));
      assertTrue(controller.hasInput("idStrategy"));
      assertTrue(controller.hasInput("tableName"));
      assertTrue(controller.getValueFor("targetPackage").toString().endsWith(".model"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testContainerInjection() throws Exception
   {
      test.getShell().setCurrentResource(project.getRoot());
      Result result = test
               .execute(("jpa-new-entity --named Customer --targetPackage org.lincoln --idStrategy AUTO --tableName CUSTOMER_TABLE"),
                        10, TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(JPAFacet.class));
      List<JavaClass<?>> allEntities = project.getFacet(JPAFacet.class).getAllEntities();
      Assert.assertEquals(1, allEntities.size());
      JavaClass<?> customerEntity = allEntities.get(0);
      Assert.assertTrue(customerEntity.hasAnnotation(Table.class));
      Assert.assertEquals("CUSTOMER_TABLE", customerEntity.getAnnotation(Table.class).getStringValue("name"));
   }
}
