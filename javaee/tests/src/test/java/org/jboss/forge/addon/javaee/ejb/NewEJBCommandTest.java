/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.ejb.MessageDriven;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Message;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetClassTransactionAttributeCommand;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetMethodTransactionAttributeCommand;
import org.jboss.forge.addon.javaee.ejb.ui.NewEJBCommand;
import org.jboss.forge.addon.javaee.jms.JMSFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewEJBCommandTest
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

   @Inject
   private FacetFactory facetFactory;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
   }

   @Test
   public void testCreateEJB() throws Exception
   {
      facetFactory.install(project, JavaSourceFacet.class);
      WizardCommandController controller = uiTestHarness.createWizardController(NewEJBCommand.class, project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "TestEJB");
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("serializable", "true");
      Assert.assertFalse(controller.canMoveToNextStep());
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      Assert.assertTrue(project.hasFacet(EJBFacet.class));
      Assert.assertFalse(project.hasFacet(JMSFacet.class));
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.TestEJB");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(Stateless.class));
      Assert.assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateMDB() throws Exception
   {
      facetFactory.install(project, JavaSourceFacet.class);
      WizardCommandController controller = uiTestHarness.createWizardController(NewEJBCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "TestEJB");
      controller.setValueFor("type", EJBType.MESSAGEDRIVEN);
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("serializable", "false");
      Assert.assertTrue(controller.isValid());
      Assert.assertFalse(controller.canExecute());

      Assert.assertTrue(controller.canMoveToNextStep());
      controller.next();
      Assert.assertFalse(controller.canExecute());

      controller.setValueFor("destType", JMSDestinationType.TOPIC);
      controller.setValueFor("destName", "destination");

      Assert.assertTrue(controller.canExecute());

      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      project = projectHelper.refreshProject(project);
      Assert.assertTrue(project.hasFacet(EJBFacet.class));
      Assert.assertTrue(project.hasFacet(JMSFacet.class));
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.TestEJB");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(MessageDriven.class));
      Assert.assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
      Assert.assertNotNull(((JavaClass<?>) javaResource.getJavaType()).getMethod("onMessage", Message.class));
   }

   @Test
   public void testSetTransactionAttributeOnEJB() throws Exception
   {
      facetFactory.install(project, JavaSourceFacet.class);
      WizardCommandController controller = uiTestHarness.createWizardController(NewEJBCommand.class, project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "TestEJB");
      controller.setValueFor("type", EJBType.MESSAGEDRIVEN);
      controller.setValueFor("targetPackage", "org.jboss.forge.test");
      controller.setValueFor("serializable", "true");
      Assert.assertTrue(controller.isValid());
      Assert.assertFalse(controller.canExecute());

      Assert.assertTrue(controller.canMoveToNextStep());
      controller.next();
      Assert.assertFalse(controller.canExecute());

      controller.setValueFor("destType", JMSDestinationType.TOPIC);
      controller.setValueFor("destName", "destination");

      Assert.assertTrue(controller.canExecute());

      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.TestEJB");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertTrue(javaResource.getJavaType().hasAnnotation(MessageDriven.class));
      Assert.assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
      Assert.assertNotNull(((JavaClass<?>) javaResource.getJavaType()).getMethod("onMessage", Message.class));

      CommandController controller2 = uiTestHarness.createCommandController(
               EJBSetClassTransactionAttributeCommand.class,
               project.getRoot());

      controller2.initialize();
      Assert.assertFalse(controller2.canExecute());
      controller2.setValueFor("targetEjb", "org.jboss.forge.test.TestEJB");
      controller2.setValueFor("type", TransactionAttributeType.NOT_SUPPORTED);
      Assert.assertTrue(controller2.canExecute());

      Assert.assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasAnnotation(TransactionAttribute.class));
      controller2.execute();
      Assert.assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasAnnotation(TransactionAttribute.class));
      Assert.assertEquals(TransactionAttributeType.NOT_SUPPORTED,
               ((JavaClass<?>) javaResource.getJavaType()).getAnnotation(TransactionAttribute.class).getEnumValue(
                        TransactionAttributeType.class));

      CommandController controller3 = uiTestHarness.createCommandController(
               EJBSetMethodTransactionAttributeCommand.class,
               project.getRoot());

      controller3.initialize();
      Assert.assertFalse(controller3.canExecute());
      controller3.setValueFor("targetEjb", "org.jboss.forge.test.TestEJB");
      controller3.setValueFor("method", "onMessage(javax.jms.Message)::void");
      controller3.setValueFor("type", TransactionAttributeType.NEVER);
      Assert.assertTrue(controller3.canExecute());

      Assert.assertFalse(((JavaClass<?>) javaResource.getJavaType()).getMethod("onMessage", Message.class)
               .hasAnnotation(TransactionAttribute.class));
      controller3.execute();
      project = projectHelper.refreshProject(project);
      Assert.assertTrue(project.hasFacet(EJBFacet.class));
      Assert.assertTrue(project.hasFacet(JMSFacet.class));
      Assert.assertTrue(((JavaClass<?>) javaResource.getJavaType()).getMethod("onMessage", Message.class)
               .hasAnnotation(
                        TransactionAttribute.class));
      Assert.assertEquals(TransactionAttributeType.NEVER,
               ((JavaClass<?>) javaResource.getJavaType()).getMethod("onMessage", Message.class)
                        .getAnnotation(TransactionAttribute.class).getEnumValue(TransactionAttributeType.class));

   }
}
