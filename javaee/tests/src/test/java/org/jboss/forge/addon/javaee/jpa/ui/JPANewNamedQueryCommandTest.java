/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the {@link JPANewNamedQueryCommand} class
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JPANewNamedQueryCommandTest
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
      projectHelper.installJPA_2_0(project);
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(JPANewNamedQueryCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof JPANewNamedQueryCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("JPA: New Named Query", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("JPA", metadata.getCategory().getSubCategory().getName());
         assertEquals(3, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("query"));
         assertTrue(controller.hasInput("targetEntity"));
      }
   }

   @Test
   public void testCreateNamedQuery() throws Exception
   {
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");
      try (CommandController controller = uiTestHarness.createCommandController(JPANewNamedQueryCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "Customer.findAll");
         controller.setValueFor("query", "select OBJ from Customer OBJ");
         controller.setValueFor("targetEntity", jpaEntity);
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertTrue(jpaEntity.exists());
      JavaClassSource javaClass = jpaEntity.getJavaType();
      Assert.assertTrue(javaClass.hasImport(NamedQueries.class));
      Assert.assertTrue(javaClass.hasImport(NamedQuery.class));
      Assert.assertTrue(javaClass.hasAnnotation(NamedQueries.class));
      AnnotationSource<JavaClassSource> namedQueries = javaClass.getAnnotation(NamedQueries.class);
      AnnotationSource<JavaClassSource>[] namedQueryArray = namedQueries.getAnnotationArrayValue();
      Assert.assertNotNull(namedQueryArray);
      Assert.assertEquals(1, namedQueryArray.length);
      AnnotationSource<JavaClassSource> namedQuery = namedQueryArray[0];
      Assert.assertEquals("Customer.findAll", namedQuery.getStringValue("name"));
      Assert.assertEquals("select OBJ from Customer OBJ", namedQuery.getStringValue("query"));
   }
}
