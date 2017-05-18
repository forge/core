/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_ENTITY_PACKAGE;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.Table;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link org.jboss.forge.addon.javaee.jpa.ui.JPANewEntityCommand} behavior
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JPANewEntityCommandTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ShellTest shellTest;

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

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof JPANewEntityCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("JPA: New Entity", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("JPA", metadata.getCategory().getSubCategory().getName());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertEquals(7, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("idStrategy"));
         assertTrue(controller.hasInput("tableName"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_ENTITY_PACKAGE));
      }
   }

   @SuppressWarnings("unchecked")
   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest
               .execute(("jpa-new-entity --named Customer --target-package org.lincoln --id-strategy AUTO --table-name CUSTOMER_TABLE"),
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
