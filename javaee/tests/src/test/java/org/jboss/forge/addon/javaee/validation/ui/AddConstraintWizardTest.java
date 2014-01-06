/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.validation.ui;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.ui.test.UITestHarness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class AddConstraintWizardTest
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
               .addBeansXML()
               .addClass(ProjectHelper.class)
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
   public void testRequiredFields() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJPA_2_0(project);
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");

      WizardCommandController wizard = testHarness.createWizardController(AddConstraintWizard.class, project.getProjectRoot());
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
      JavaClass javaClass = (JavaClass) jpaEntity.getJavaSource();
      Field<JavaClass> field = javaClass.getField("id");
      Annotation<JavaClass> pattern = field.getAnnotation(Pattern.class);
      Assert.assertNotNull(pattern);
      Assert.assertEquals("[0-9]", pattern.getStringValue("regexp"));

   }
}
