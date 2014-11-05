/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.enterprise.context.NormalScope;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewBeanCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
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
   private UITestHarness testHarness;

   @Inject
   private ProjectHelper projectHelper;

   @Test
   public void testCreateNewBean() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_0(project);
      CommandController controller = testHarness.createCommandController(NewBeanCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyServiceBean");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("scoped", BeanScope.SESSION.name());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(SessionScoped.class));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewBeanWithAlternativeAndNamed() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_0(project);
      CommandController controller = testHarness.createCommandController(NewBeanCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyServiceBean");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("scoped", BeanScope.SESSION.name());
      controller.setValueFor("alternative", true);
      controller.setValueFor("withNamed", true);
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(SessionScoped.class));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewBeanWithNoAlternativeAndNamed() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_0(project);
      CommandController controller = testHarness.createCommandController(NewBeanCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyServiceBean");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("alternative", false);
      controller.setValueFor("withNamed", false);
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewBeanWithQualifier() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_0(project);
      CommandController controller = testHarness.createCommandController(NewBeanCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyServiceBean");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("qualifier", "javax.inject.Named");
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(SessionScoped.class));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewBeanCustomScope() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_0(project);
      JavaAnnotationSource ann = Roaster.create(JavaAnnotationSource.class).setName("MyCustomScope")
               .setPackage("org.jboss.forge.test.scope");
      ann.addAnnotation(NormalScope.class);
      project.getFacet(JavaSourceFacet.class).saveJavaSource(ann);
      CommandController controller = testHarness.createCommandController(NewBeanCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyCustomServiceBean");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("scoped", BeanScope.CUSTOM.name());
      Assert.assertFalse(controller.isValid());
      controller.setValueFor("customScopeAnnotation", ann.getQualifiedName());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyCustomServiceBean");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      Assert.assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
   }
}
