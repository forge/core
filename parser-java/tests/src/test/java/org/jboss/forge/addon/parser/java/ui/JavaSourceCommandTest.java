/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.JavaAnnotation;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.JavaEnum;
import org.jboss.forge.roaster.model.JavaInterface;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JavaSourceCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private UITestHarness testHarness;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testCreateAnnotation() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      CommandController controller = getInitializedController(JavaAnnotationCommand.class, project.getRoot());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaAnnotation.class)));

      // overwriting the annotation file
      controller = getInitializedController(JavaAnnotationCommand.class, project.getRoot());
      Assert.assertFalse(controller.isValid());
      controller.setValueFor("overwrite", "true");
      Assert.assertTrue(controller.isValid());
      result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
   }

   @Test
   public void testCreateEnum() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      CommandController controller = getInitializedController(JavaEnumCommand.class, project.getRoot());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaEnum.class)));

      // overwriting the enum file
      controller = getInitializedController(JavaEnumCommand.class, project.getRoot());
      Assert.assertFalse(controller.isValid());
      controller.setValueFor("overwrite", "true");
      Assert.assertTrue(controller.isValid());
      result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
   }

   @Test
   public void testCreateClass() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      CommandController controller = getInitializedController(JavaClassCommand.class, project.getRoot());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));

      // overwriting the class file
      controller = getInitializedController(JavaClassCommand.class, project.getRoot());
      Assert.assertFalse(controller.isValid());
      controller.setValueFor("overwrite", "true");
      Assert.assertTrue(controller.isValid());
      result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
   }

   @Test
   public void testCreateException() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);

      CommandController controller = testHarness.createCommandController(JavaExceptionCommand.class, project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyException");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");

      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyException");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
   }

   @Test
   public void testCreateInterface() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      CommandController controller = getInitializedController(JavaInterfaceCommand.class, project.getRoot());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaInterface.class)));

      // overwriting the interface file
      controller = getInitializedController(JavaInterfaceCommand.class, project.getRoot());
      Assert.assertFalse(controller.isValid());
      controller.setValueFor("overwrite", "true");
      Assert.assertTrue(controller.isValid());
      result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
   }

   private CommandController getInitializedController(Class<? extends UICommand> clazz, Resource<?>... initialSelection)
            throws Exception
   {
      CommandController controller = testHarness.createCommandController(clazz, initialSelection);
      controller.initialize();
      controller.setValueFor("named", "CreditCardType");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      return controller;
   }

}
