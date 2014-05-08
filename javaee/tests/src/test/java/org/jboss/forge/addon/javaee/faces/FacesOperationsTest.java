/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacesOperationsTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee")
               );
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private FacesOperations operations;

   @Test
   public void testCreateBackingBeanInDirectory() throws Exception
   {
      DirectoryResource dir = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      JavaResource bean = operations.newConverter(dir, "SampleBean", "org.example");

      Assert.assertEquals("SampleBean.java", bean.getName());
      Assert.assertEquals("SampleBean", bean.getJavaType().getName());
      Assert.assertEquals("org.example", bean.getJavaType().getPackage());
      Assert.assertTrue(bean.exists());
   }

   @SuppressWarnings("deprecation")
   @Test
   public void testCreateBackingBeanInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);

      JavaResource bean = operations.newConverter(project, "SampleBean", "org.example");
      Assert.assertTrue(bean.exists());

      Assert.assertEquals("SampleBean.java", bean.getName());
      Resource<?> child = project.getRootDirectory().getChild("src/main/java/org/example/SampleBean.java");
      Assert.assertTrue(child.exists());
      Assert.assertTrue(child instanceof JavaResource);
      Assert.assertEquals("SampleBean", ((JavaResource) child).getJavaType().getName());
      Assert.assertEquals("org.example", ((JavaResource) child).getJavaType().getPackage());
   }

   @Test
   public void testCreateConverterInDirectory() throws Exception
   {
      DirectoryResource dir = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      JavaResource converter = operations.newConverter(dir, "SampleConverter", "org.example");

      Assert.assertEquals("SampleConverter.java", converter.getName());
      Assert.assertEquals("SampleConverter", converter.getJavaType().getName());
      Assert.assertEquals("org.example", converter.getJavaType().getPackage());
      Assert.assertTrue(converter.exists());
   }

   @SuppressWarnings("deprecation")
   @Test
   public void testCreateConverterInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);

      JavaResource converter = operations.newConverter(project, "SampleConverter", "org.example");
      Assert.assertTrue(converter.exists());

      Assert.assertEquals("SampleConverter.java", converter.getName());
      Resource<?> child = project.getRootDirectory().getChild("src/main/java/org/example/SampleConverter.java");
      Assert.assertTrue(child.exists());
      Assert.assertTrue(child instanceof JavaResource);
      Assert.assertEquals("SampleConverter", ((JavaResource) child).getJavaType().getName());
      Assert.assertEquals("org.example", ((JavaResource) child).getJavaType().getPackage());
   }

   @Test
   public void testCreateValidatorInDirectory() throws Exception
   {
      DirectoryResource dir = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      JavaResource validator = operations.newValidator(dir, "SampleValidator", "org.example");

      Assert.assertEquals("SampleValidator.java", validator.getName());
      Assert.assertEquals("SampleValidator", validator.getJavaType().getName());
      Assert.assertEquals("org.example", validator.getJavaType().getPackage());
      Assert.assertTrue(validator.exists());
   }

   @Test
   public void testCreateValidatorInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);

      JavaResource validator = operations.newValidator(project, "SampleValidator", "org.example");
      Assert.assertTrue(validator.exists());

      Assert.assertEquals("SampleValidator.java", validator.getName());
      Resource<?> child = project.getRoot().getChild("src/main/java/org/example/SampleValidator.java");
      Assert.assertTrue(child.exists());
      Assert.assertTrue(child instanceof JavaResource);
      Assert.assertEquals("SampleValidator", ((JavaResource) child).getJavaType().getName());
      Assert.assertEquals("org.example", ((JavaResource) child).getJavaType().getPackage());
   }

   @Test
   public void testCreateValidatorMethod() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);

      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = sourceFacet
               .saveJavaSource(Roaster.parse(JavaClassSource.class, "package org.example; public class DemoBean {}"));

      MethodSource<?> method = operations.addValidatorMethod(resource, "validateUsername");
      Assert.assertEquals(3, method.getParameters().size());

      JavaClassSource source = resource.getJavaType();
      Assert.assertEquals(1, source.getMethods().size());
      Assert.assertEquals(method.toSignature(), source.getMethods().get(0).toSignature());
   }

}
