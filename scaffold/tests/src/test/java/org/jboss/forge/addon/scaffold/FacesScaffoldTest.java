/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.scaffold.faces.FacesScaffoldProvider;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
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
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:scaffold-faces"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(ProjectHelper.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   ProjectFactory projectFactory;

   @Inject
   ShellTest shellTest;

   @Test
   public void testScaffoldSetup() throws Exception
   {
      shellTest.execute("project-new --named demo" + System.currentTimeMillis(), 5, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-entity --named Customer", 5, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS);
      Result result = shellTest.execute("scaffold-setup", 10, TimeUnit.SECONDS);
      Assert.assertThat(result, is(instanceOf(CompositeResult.class)));
   }

   @Test
   public void shouldCreateOneErrorPageForEachErrorCode() throws Exception
   {
      shellTest.execute("project-new --named demo" + System.currentTimeMillis(), 10, TimeUnit.SECONDS);
      shellTest.execute("servlet-setup --servletVersion 3.1", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-entity --named Customer", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-entity --named Publisher", 10, TimeUnit.SECONDS);
      shellTest.execute("jpa-new-field --named firstName", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.execute("scaffold-setup", 10, TimeUnit.SECONDS), not(instanceOf(Failed.class)));
      Project project = projectFactory.findProject(shellTest.getShell().getCurrentResource());
      Assert.assertTrue(project.hasFacet(ServletFacet_3_1.class));
      ServletFacet_3_1 servletFacet = project.getFacet(ServletFacet_3_1.class);
      Assert.assertNotNull(servletFacet.getConfig());

      String entityPackageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      Result scaffoldGenerate1 = shellTest
               .execute(("scaffold-generate --webRoot /admin --targets " + entityPackageName + ".Customer"), 10,
                        TimeUnit.SECONDS);
      Assert.assertThat(scaffoldGenerate1, not(instanceOf(Failed.class)));

      Assert.assertEquals(2, servletFacet.getConfig().getAllErrorPage().size());

      Result scaffoldGenerate2 = shellTest
               .execute(("scaffold-generate --webRoot /admin --targets " + entityPackageName + ".Publisher"), 10,
                        TimeUnit.SECONDS);
      Assert.assertThat(scaffoldGenerate2, not(instanceOf(Failed.class)));
      Assert.assertEquals(2, servletFacet.getConfig().getAllErrorPage().size());
   }
}
