/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import static org.hamcrest.CoreMatchers.*;

import java.io.Serializable;

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
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.*;
import org.jboss.forge.roaster.model.source.JavaClassSource;
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
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"));

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
      try (CommandController controller = getInitializedController(JavaNewAnnotationCommand.class, project.getRoot()))
      {
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaAnnotation.class)));
      }
      // overwriting the annotation file
      try (CommandController controller = getInitializedController(JavaNewAnnotationCommand.class, project.getRoot()))
      {
         Assert.assertFalse(controller.isValid());
         controller.setValueFor("overwrite", "true");
         Assert.assertTrue(controller.isValid());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   @Test
   public void testCreateEnum() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = getInitializedController(JavaNewEnumCommand.class, project.getRoot()))
      {
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaEnum.class)));
      }
      // overwriting the enum file
      try (CommandController controller = getInitializedController(JavaNewEnumCommand.class, project.getRoot()))
      {
         Assert.assertFalse(controller.isValid());
         controller.setValueFor("overwrite", "true");
         Assert.assertTrue(controller.isValid());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   @Test
   public void testCreateClass() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = getInitializedController(JavaNewClassCommand.class, project.getRoot()))
      {
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      }
      // overwriting the class file
      try (CommandController controller = getInitializedController(JavaNewClassCommand.class, project.getRoot()))
      {
         Assert.assertFalse(controller.isValid());
         controller.setValueFor("overwrite", "true");
         Assert.assertTrue(controller.isValid());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   @Test
   public void testCreateClassAbstract() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = getInitializedController(JavaNewClassCommand.class, project.getRoot()))
      {
         controller.setValueFor("abstract", true);
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         JavaType<?> javaType = javaResource.getJavaType();
         Assert.assertThat(javaType, is(instanceOf(JavaClass.class)));
         JavaClass<?> javaClass = (JavaClass<?>) javaType;
         Assert.assertThat(javaClass.isAbstract(), is(true));
         Assert.assertThat(javaClass.isFinal(), is(false));
      }
   }

   @Test
   public void testCreateClassFinal() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = getInitializedController(JavaNewClassCommand.class, project.getRoot()))
      {
         controller.setValueFor("final", true);
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         JavaType<?> javaType = javaResource.getJavaType();
         Assert.assertThat(javaType, is(instanceOf(JavaClass.class)));
         JavaClass<?> javaClass = (JavaClass<?>) javaType;
         Assert.assertThat(javaClass.isAbstract(), is(false));
         Assert.assertThat(javaClass.isFinal(), is(true));
      }
   }

   @Test
   public void testCreateClassSerializable() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = getInitializedController(JavaNewClassCommand.class, project.getRoot()))
      {
         controller.setValueFor("serializable", true);
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         JavaType<?> javaType = javaResource.getJavaType();
         Assert.assertThat(javaType, is(instanceOf(JavaClass.class)));
         JavaClass<?> javaClass = (JavaClass<?>) javaType;
         Assert.assertThat(javaClass.isAbstract(), is(false));
         Assert.assertThat(javaClass.isFinal(), is(false));
         Assert.assertThat(javaClass.hasInterface(Serializable.class), is(true));
      }
   }

   @Test
   public void testCreateException() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);

      try (CommandController controller = testHarness.createCommandController(JavaNewExceptionCommand.class,
               project.getRoot()))
      {
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
   }

   @Test
   public void testCreateInterface() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      try (CommandController controller = getInitializedController(JavaNewInterfaceCommand.class, project.getRoot()))
      {
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaInterface.class)));
      }
      // overwriting the interface file
      try (CommandController controller = getInitializedController(JavaNewInterfaceCommand.class, project.getRoot()))
      {
         Assert.assertFalse(controller.isValid());
         controller.setValueFor("overwrite", "true");
         Assert.assertTrue(controller.isValid());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   @Test
   public void testJavaSourceDecorator() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);

      try (CommandController controller = getInitializedController(JavaNewClassCommand.class, project.getRoot()))
      {
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         Assert.assertNotNull(javaResource);
         Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));

         final String initialContent = javaResource.getContents();
         final UICommand command = controller.getCommand();
         Assert.assertTrue(command instanceof AbstractJavaSourceCommand);
         final AbstractJavaSourceCommand<JavaClassSource> javaSourceCommand = (AbstractJavaSourceCommand) command;

         // set a delegate
         final String propertyName = "name";
         javaSourceCommand.setJavaSourceDecorator((context, project1, source) -> {
            source.addProperty(String.class, propertyName);
            return source;
         });

         // execute the command again and check the new source content
         controller.setValueFor("overwrite", "true");
         controller.execute();
         javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
         final String newContent = javaResource.getContents();
         Assert.assertFalse(initialContent.equals(newContent));
         Assert.assertTrue(newContent.contains(propertyName));
      }
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
