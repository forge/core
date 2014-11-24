/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.inject.Inject;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
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
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the {@link NewNamedQueryCommand} class
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewNamedQueryCommandTest
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
   public void testCreateNamedQuery() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJPA_2_0(project);
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");
      try (CommandController controller = testHarness.createCommandController(NewNamedQueryCommand.class,
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
