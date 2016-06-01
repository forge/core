/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategyFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.PropertySource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class RestEndpointFromEntityCommandTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ProjectHelper projectHelper;

   @Test
   public void testCreateRESTGeneration() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJAXRS_2_0(project, RestConfigurationStrategyFactory.createUsingWebXml("/rest"));
      projectHelper.installEJB_3_2(project);
      // Execute JPA:Setup
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPASetupWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.execute();
      }
      project = projectHelper.refreshProject(project);
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (CommandController controller = uiTestHarness.createCommandController(RestEndpointFromEntityCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("targets", Arrays.asList(entity.getJavaType()));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource restResource = facet.getJavaResource("unknown.rest.CustomerEndpoint");
      Assert.assertTrue(restResource.exists());
      Assert.assertThat(restResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClassSource restClass = restResource.getJavaType();
      Assert.assertFalse(restClass.hasSyntaxErrors());
      Method<?, ?> method = restClass.getMethod("create", "unknown.model.Customer");
      Annotation<?> consumes = method.getAnnotation(Consumes.class);
      Assert.assertEquals(MediaType.APPLICATION_JSON, consumes.getStringValue());
   }

   @Test
   public void testCreateRESTForPrimitiveType() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJAXRS_2_0(project, RestConfigurationStrategyFactory.createUsingWebXml("/rest"));
      // Execute JPA:Setup
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPASetupWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.execute();
      }
      projectHelper.installEJB_3_2(project);
      project = projectHelper.refreshProject(project);
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaClassSource javaClass = entity.getJavaType();
      PropertySource<JavaClassSource> idProperty = javaClass.getProperty("id");
      idProperty.setType("long");
      javaClass.removeMethod(javaClass.getMethod("equals", Object.class));
      javaClass.removeMethod(javaClass.getMethod("hashCode"));
      Refactory.createHashCodeAndEquals(javaClass, idProperty.getField());
      entity.setContents(javaClass);
      try (CommandController controller = uiTestHarness.createCommandController(RestEndpointFromEntityCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("targets", Arrays.asList(entity.getJavaType()));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource restResource = facet.getJavaResource("unknown.rest.CustomerEndpoint");
      Assert.assertTrue(restResource.exists());
      Assert.assertThat(restResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClassSource restClass = restResource.getJavaType();
      Assert.assertFalse(restClass.hasSyntaxErrors());
      Method<?, ?> method = restClass.getMethod("create", "unknown.model.Customer");
      Annotation<?> consumes = method.getAnnotation(Consumes.class);
      Assert.assertEquals(MediaType.APPLICATION_JSON, consumes.getStringValue());
   }
}