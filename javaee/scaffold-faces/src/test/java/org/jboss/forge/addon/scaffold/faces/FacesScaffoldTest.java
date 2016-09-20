/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * Test class for {@link FacesScaffoldProvider}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacesScaffoldTest
{
   ProjectFactory projectFactory;
   ShellTest shellTest;
   Project project;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      shellTest = SimpleContainer.getServices(getClass().getClassLoader(), ShellTest.class).get();
      project = projectFactory.createTempProject(
               Arrays.<Class<? extends ProjectFacet>>asList(JavaSourceFacet.class, WebResourcesFacet.class));
      shellTest.getShell().setCurrentResource(project.getRoot());
   }

   @Test
   public void testScaffoldSetup() throws Exception
   {
      shellTest.execute("jpa-new-entity --named Customer", 15, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS);
      Result result = shellTest.execute("scaffold-setup", 10, TimeUnit.SECONDS);
      Assert.assertThat(result, is(instanceOf(CompositeResult.class)));
   }

   @Test
   public void shouldCreateOneErrorPageForEachErrorCode() throws Exception
   {
      shellTest.execute("servlet-setup --servlet-version 3.1", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-setup", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-entity --named Customer", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-entity --named Publisher", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS);
      Result result = shellTest.execute("scaffold-setup --provider Faces", 10, TimeUnit.SECONDS);
      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Project project = projectFactory.findProject(shellTest.getShell().getCurrentResource());
      Assert.assertTrue(project.hasFacet(ServletFacet_3_1.class));
      ServletFacet_3_1 servletFacet = project.getFacet(ServletFacet_3_1.class);
      Assert.assertNotNull(servletFacet.getConfig());

      String entityPackageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      Result scaffoldGenerate1 = shellTest
               .execute(("scaffold-generate --web-root /admin --targets " + entityPackageName
                        + ".Customer"), 10,
                        TimeUnit.SECONDS);
      Assert.assertThat(scaffoldGenerate1, not(instanceOf(Failed.class)));

      Assert.assertEquals(2, servletFacet.getConfig().getAllErrorPage().size());

      Result scaffoldGenerate2 = shellTest
               .execute(("scaffold-generate --web-root /admin --targets " + entityPackageName
                        + ".Publisher"), 10,
                        TimeUnit.SECONDS);
      Assert.assertThat(scaffoldGenerate2, not(instanceOf(Failed.class)));
      Assert.assertEquals(2, servletFacet.getConfig().getAllErrorPage().size());
   }

   @Test
   public void shouldScaffoldEntity() throws Exception
   {
      Assert.assertThat(shellTest.execute("javaee-setup --java-ee-version 7", 10, TimeUnit.SECONDS),
               not(instanceOf(Failed.class)));
      Assert.assertThat(shellTest.execute("jpa-setup", 10, TimeUnit.SECONDS), not(instanceOf(Failed.class)));
      Assert.assertThat(shellTest.execute("jpa-new-entity --named Customer", 10, TimeUnit.SECONDS),
               not(instanceOf(Failed.class)));
      Assert.assertThat(shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS),
               not(instanceOf(Failed.class)));
      Result result = shellTest.execute("scaffold-setup --provider Faces", 10, TimeUnit.SECONDS);
      Assert.assertThat(result, not(instanceOf(Failed.class)));

      Project project = projectFactory.findProject(shellTest.getShell().getCurrentResource());
      String entityPackageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      result = shellTest.execute(
               "scaffold-generate --provider Faces --targets " + entityPackageName + ".Customer", 10, TimeUnit.SECONDS);
      Assert.assertThat(result, not(instanceOf(Failed.class)));
   }

   @After
   public void tearDown() throws Exception
   {
      if (project != null)
         project.getRoot().delete(true);
      shellTest.close();
   }
}
