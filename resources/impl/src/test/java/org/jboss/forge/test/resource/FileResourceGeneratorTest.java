package org.jboss.forge.test.resource;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.UnknownFileResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class FileResourceGeneratorTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge:facets", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addPackages(true, ResourceFactory.class.getPackage())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:facets", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Test
   public void testCreateUnknownFileResource() throws Exception
   {
      FileResource<?> resource = factory.create(new File(UUID.randomUUID().toString())).reify(FileResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(UnknownFileResource.class, resource.getClass());
   }

   @Test
   public void testCreateFileResource() throws Exception
   {
      File file = File.createTempFile("temp", "file");
      FileResource<?> resource = factory.create(file).reify(FileResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(UnknownFileResource.class, resource.getClass());
   }

   @Test
   public void testCreateDirectoryResource() throws Exception
   {
      File dir = File.createTempFile("temp", "file");
      dir.delete();
      dir.mkdir();

      Resource<File> resource = factory.create(dir);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource.exists());
      Assert.assertTrue(resource instanceof DirectoryResource);
   }

   @Test
   public void testCreateDirectoryResourceViaRiefy() throws Exception
   {
      File dir = File.createTempFile("temp", "file");
      dir.delete();
      dir.mkdir();

      File child = new File(dir, "child");
      child.mkdir();

      DirectoryResource resource = factory.create(dir).reify(DirectoryResource.class);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource.exists());

      DirectoryResource childResource = resource.getChild("child").reify(DirectoryResource.class);
      Assert.assertNotNull(childResource);
      Assert.assertTrue(childResource.exists());
   }

}