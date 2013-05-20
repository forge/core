package org.jboss.forge.addon.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.resource.addon.FileResourceImpl;
import org.jboss.shrinkwrap.api.ShrinkWrap;
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
   @Dependencies({
            @Addon(name = "org.jboss.forge.addon:facets", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Test
   public void testCreateUnknownFileResource() throws Exception
   {
      FileResource<?> resource = factory.create(new File(UUID.randomUUID().toString())).reify(FileResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(FileResourceImpl.class, resource.getClass());
   }

   @Test
   public void testCreateFileResource() throws Exception
   {
      File file = File.createTempFile("temp", "file");
      FileResource<?> resource = factory.create(file).reify(FileResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(FileResourceImpl.class, resource.getClass());
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

   @Test
   public void testFileSize() throws Exception
   {
      File file = File.createTempFile("temp", "file");

      FileOutputStream fos = new FileOutputStream(file);
      Streams.write(new ByteArrayInputStream("Test".getBytes()), fos);
      fos.close();

      FileResource<?> fileResource = factory.create(file).reify(FileResource.class);

      Assert.assertEquals(file.length(), fileResource.getSize());
      file.delete();
   }

   @Test(expected = ContainerException.class)
   public void testDirectorySize() throws Exception
   {
      File dir = File.createTempFile("temp", "file");
      dir.delete();
      dir.mkdir();
      factory.create(dir).reify(DirectoryResource.class).getSize();
   }

   @Test
   public void testFileFlags() throws Exception
   {
      File tempFile = File.createTempFile("temp", "file");
      tempFile.deleteOnExit();
      FileResource<?> resource = factory.create(tempFile).reify(FileResource.class);
      Assert.assertFalse(resource.isExecutable());
      Assert.assertTrue(resource.isReadable());
      Assert.assertTrue(resource.isWritable());
   }

   @Test
   public void testDirectoryFlags() throws Exception
   {
      File dir = File.createTempFile("temp", "file");
      dir.delete();
      dir.mkdir();
      dir.deleteOnExit();
      DirectoryResource resource = factory.create(dir).reify(DirectoryResource.class);
      Assert.assertFalse(resource.isExecutable());
      Assert.assertFalse(resource.isReadable());
      Assert.assertFalse(resource.isWritable());
   }

   @Test
   public void testRenameResource() throws Exception
   {
      File file = File.createTempFile("temp", "file");
      FileResource<?> resource = factory.create(file).reify(FileResource.class);
      Assert.assertNotNull(resource);
      FileResource<?> child = resource.getParent().getChild("testFile").reify(FileResource.class);
      resource.renameTo(child);
      Assert.assertEquals(child.getFullyQualifiedName(), resource.getFullyQualifiedName());
   }

}