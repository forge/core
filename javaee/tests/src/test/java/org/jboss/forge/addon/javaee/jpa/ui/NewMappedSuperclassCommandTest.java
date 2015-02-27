/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.persistence.MappedSuperclass;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewMappedSuperclassCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:javaee"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addClass(ProjectHelper.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private FacetFactory facetFactory;

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
      CommandController controller = uiTestHarness.createCommandController(NewMappedSuperclassCommand.class,
               project.getRoot());
      controller.initialize();
      // Checks the command metadata
      assertTrue(controller.getCommand() instanceof NewMappedSuperclassCommand);
      UICommandMetadata metadata = controller.getMetadata();
      assertEquals("JPA: New Mapped Superclass", metadata.getName());
      assertEquals("Java EE", metadata.getCategory().getName());
      assertEquals("JPA", metadata.getCategory().getSubCategory().getName());
      assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
      assertEquals(3, controller.getInputs().size());
      assertTrue(controller.hasInput("named"));
      assertTrue(controller.hasInput("targetPackage"));
      assertTrue(controller.hasInput("overwrite"));
      assertTrue(controller.getValueFor("targetPackage").toString().endsWith(".model"));
   }

   @Test
   public void testCreateMappedSuperclass() throws Exception
   {
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = uiTestHarness.createCommandController(NewMappedSuperclassCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "CreditCardType");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      Assert.assertTrue(javaResource.exists());
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(MappedSuperclass.class));
   }

   @Test
   public void testCreateMappedSuperclassDefaultPackage() throws Exception
   {
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = uiTestHarness.createCommandController(NewMappedSuperclassCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "CreditCardType");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      JavaResource javaResource = facet.getJavaResource(packageName + ".CreditCardType");
      Assert.assertTrue(javaResource.exists());
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(MappedSuperclass.class));
   }
}
