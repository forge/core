/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.validation.CoreConstraints;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
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
public class ValidationAddConstraintWizardTest
{

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installValidation(project);
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(ValidationAddConstraintWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof ValidationAddConstraintWizard);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("Constraint: Add", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("Bean Validation", metadata.getCategory().getSubCategory().getName());
         assertEquals(1, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("javaClass"));
      }
   }

   @Test
   public void testRequiredFields() throws Exception
   {
      projectHelper.installJPA_2_0(project);
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");

      WizardCommandController wizard = uiTestHarness.createWizardController(ValidationAddConstraintWizard.class,
               project.getRoot());
      wizard.initialize();
      // Page 1
      {
         wizard.setValueFor("javaClass", jpaEntity);
         Assert.assertTrue(wizard.canMoveToNextStep());
      }
      wizard.next().initialize();
      // Page 2
      {
         wizard.setValueFor("onProperty", "id");
         wizard.setValueFor("constraint", CoreConstraints.PATTERN);
         Assert.assertTrue(wizard.canMoveToNextStep());
      }
      wizard.next().initialize();
      // Page 3
      {
         Assert.assertFalse(wizard.isValid());
         wizard.setValueFor("regexp", "[0-9]");
         Assert.assertTrue(wizard.isValid());
      }
      wizard.execute();
      JavaClassSource javaClass = jpaEntity.getJavaType();
      FieldSource<JavaClassSource> field = javaClass.getField("id");
      AnnotationSource<JavaClassSource> pattern = field.getAnnotation(Pattern.class);
      Assert.assertNotNull(pattern);
      Assert.assertEquals("[0-9]", pattern.getStringValue("regexp"));

   }

   @Test
   public void testRegularExpressionField() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJPA_2_0(project);
      projectHelper.installValidation(project);
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");

      WizardCommandController wizard = uiTestHarness.createWizardController(ValidationAddConstraintWizard.class,
               project.getRoot());
      wizard.initialize();
      // Page 1
      {
         wizard.setValueFor("javaClass", jpaEntity);
         Assert.assertTrue(wizard.canMoveToNextStep());
      }
      wizard.next().initialize();
      // Page 2
      {
         wizard.setValueFor("onProperty", "id");
         wizard.setValueFor("constraint", CoreConstraints.PATTERN);
         Assert.assertTrue(wizard.canMoveToNextStep());
      }
      wizard.next().initialize();
      // Page 3
      {
         Assert.assertFalse(wizard.isValid());
         wizard.setValueFor("regexp", "^\\d{9}[\\d|X]$");
         Assert.assertTrue(wizard.isValid());
      }
      wizard.execute();
      JavaClassSource javaClass = jpaEntity.getJavaType();
      FieldSource<JavaClassSource> field = javaClass.getField("id");
      AnnotationSource<JavaClassSource> pattern = field.getAnnotation(Pattern.class);
      Assert.assertNotNull(pattern);
      Assert.assertEquals("^\\d{9}[\\d|X]$", pattern.getStringValue("regexp"));

   }
}
