/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.rest.RestFacet_2_0;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class RestSetupTest
{
   private static final Dependency JAVAEE6 = DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0")
            .setScopeType("import")
            .setPackaging("pom").setVersion("3.0.2.Final");

   private static final Dependency JAVAEE7 = DependencyBuilder.create().setGroupId("javax").setArtifactId("javaee-api")
            .setVersion("7.0")
            .setScopeType("provided");

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();// .addClass(ProjectHelper.class);
   }

   @Inject
   private ShellTest shell;

   @Inject
   private ProjectFactory projectFactory;

   @After
   public void tearDown() throws Exception
   {
      shell.close();
   }

   @Test
   public void testProjectSetup() throws Exception
   {
      Project tempProject = projectFactory.createTempProject(Arrays
               .<Class<? extends ProjectFacet>> asList(JavaSourceFacet.class));
      shell.getShell().setCurrentResource(tempProject.getRoot());
      shell.execute("rest-setup --jaxrs-version 2.0", 15, TimeUnit.SECONDS);
      Resource<?> currentResource = shell.getShell().getCurrentResource();
      Project project = projectFactory.findProject(currentResource);
      Assert.assertTrue(project.hasFacet(RestFacet_2_0.class));
   }

   @Test
   public void testFORGE2127() throws Exception
   {
      Project tempProject = projectFactory.createTempProject(Arrays
               .<Class<? extends ProjectFacet>> asList(JavaSourceFacet.class));
      shell.getShell().setCurrentResource(tempProject.getRoot());
      shell.execute("javaee-setup --java-ee-version 7", 15, TimeUnit.SECONDS);
      shell.execute("rest-setup --jaxrs-version 2.0", 5, TimeUnit.MINUTES);
      Resource<?> currentResource = shell.getShell().getCurrentResource();
      Project project = projectFactory.findProject(currentResource);
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      Assert.assertTrue("Should have added managed JavaEE7 pom", dependencyFacet.hasDirectManagedDependency(JAVAEE7));
      Assert.assertFalse("Should not have added managed JavaEE6 pom",
               dependencyFacet.hasDirectManagedDependency(JAVAEE6));
   }
}