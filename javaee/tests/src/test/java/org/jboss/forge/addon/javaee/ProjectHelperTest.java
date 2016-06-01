/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ejb.EJBFacet_3_2;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.JPAFacet_2_0;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ProjectHelperTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ProjectHelper projectHelper;

   @Test
   public void testInjection()
   {
      Assert.assertNotNull(projectHelper);
   }

   @Test
   public void testJavaLibraryProjectCreation()
   {
      Project project = projectHelper.createJavaLibraryProject();
      Assert.assertTrue(project.hasFacet(MetadataFacet.class));
      Assert.assertTrue(project.hasFacet(PackagingFacet.class));
      Assert.assertTrue(project.hasFacet(DependencyFacet.class));
      Assert.assertTrue(project.hasFacet(ResourcesFacet.class));
      Assert.assertTrue(project.hasFacet(JavaSourceFacet.class));
      Assert.assertTrue(project.hasFacet(JavaCompilerFacet.class));
      Assert.assertFalse(project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   public void testWebProjectCreation()
   {
      Project project = projectHelper.createWebProject();
      Assert.assertTrue(project.hasFacet(MetadataFacet.class));
      Assert.assertTrue(project.hasFacet(PackagingFacet.class));
      Assert.assertTrue(project.hasFacet(DependencyFacet.class));
      Assert.assertTrue(project.hasFacet(ResourcesFacet.class));
      Assert.assertTrue(project.hasFacet(JavaSourceFacet.class));
      Assert.assertTrue(project.hasFacet(JavaCompilerFacet.class));
      Assert.assertTrue(project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   public void testEJBSetup()
   {
      Project project = projectHelper.createWebProject();
      EJBFacet_3_2 ejb = projectHelper.installEJB_3_2(project);
      Assert.assertNotNull(ejb);
      Assert.assertTrue(project.hasFacet(EJBFacet_3_2.class));
   }

   @Test
   public void testJPASetup()
   {
      Project project = projectHelper.createWebProject();
      JPAFacet_2_0 jpa = projectHelper.installJPA_2_0(project);
      Assert.assertNotNull(jpa);
      Assert.assertTrue(project.hasFacet(JPAFacet.class));
   }

   @Test(expected = BuildException.class)
   public void testJPAEntityCreationWithoutJPASetup() throws Exception
   {
      Project project = projectHelper.createWebProject();
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");
      Assert.assertTrue(jpaEntity.exists());
      Resource<?> build = project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build();
      Assert.assertNotNull(build);
      Assert.assertFalse(build.exists());
   }

   @Test
   public void testJPAEntityCreation() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJPA_2_0(project);
      JavaResource jpaEntity = projectHelper.createJPAEntity(project, "Customer");
      Assert.assertTrue(jpaEntity.exists());
      Resource<?> build = project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build();
      Assert.assertNotNull(build);
      Assert.assertTrue("Build artifact does not exist", build.exists());
   }

   @Test
   public void testEnumCreation() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installJPA_2_0(project);
      JavaResource enumEntity = projectHelper.createEmptyEnum(project, "CustomerType");
      Assert.assertTrue(enumEntity.exists());
      Assert.assertTrue(enumEntity.getJavaType().isEnum());
      Resource<?> build = project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build();
      Assert.assertNotNull(build);
      Assert.assertTrue("Build artifact does not exist", build.exists());
   }
}