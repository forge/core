/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.JPAFacet_2_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;

/**
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
@RunWith(Arquillian.class)
public class FacesScaffoldProviderTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:scaffold-faces")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addClass(ProjectHelper.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:scaffold-faces")
               );
   }

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private ShellTest shellTest;

   @Test
   public void shouldCreateOneErrorPageForEachErrorCode() throws TimeoutException
   {

      Project webProject = projectHelper.createWebProject();
      shellTest.getShell().setCurrentResource(webProject.getRoot());
      projectHelper.installServlet_3_1(webProject);
      Assert.assertTrue(webProject.hasFacet(ServletFacet_3_1.class));
      projectHelper.installJPA_2_0(webProject);
      Assert.assertTrue(webProject.hasFacet(JPAFacet_2_0.class));

      ServletFacet_3_1 servletFacet = webProject.getFacet(ServletFacet_3_1.class);
      WebAppDescriptor config = servletFacet.getConfig();
      Assert.assertNotNull(config);

      Result categoryEntity = shellTest.execute(("jpa-new-entity --named Category"), 10, TimeUnit.SECONDS);
      Assert.assertThat(categoryEntity, not(instanceOf(Failed.class)));

      Result categoryNameField = shellTest.execute(("jpa-new-field --named name --length 100"), 10, TimeUnit.SECONDS);
      Assert.assertThat(categoryNameField, not(instanceOf(Failed.class)));

      Result scaffoldGenerate1 = shellTest
               .execute(("scaffold-generate --webRoot /admin --targets org.testwebxml.model.Category"), 10,
                        TimeUnit.SECONDS);
      Assert.assertThat(scaffoldGenerate1, not(instanceOf(Failed.class)));

      Result PublisherEntity = shellTest.execute(("jpa-new-entity --named Publisher"), 10, TimeUnit.SECONDS);
      Assert.assertThat(PublisherEntity, not(instanceOf(Failed.class)));

      Result PublisherNameField = shellTest.execute(("jpa-new-field --named name --length 30"), 10, TimeUnit.SECONDS);
      Assert.assertThat(PublisherNameField, not(instanceOf(Failed.class)));

      Result scaffoldGenerate2 = shellTest
               .execute(("scaffold-generate --webRoot /admin --targets org.testwebxml.model.Publisher"), 10,
                        TimeUnit.SECONDS);
      Assert.assertThat(scaffoldGenerate2, not(instanceOf(Failed.class)));

      config = servletFacet.getConfig();
      Assert.assertEquals(2, config.getAllErrorPage().size());
   }
}
