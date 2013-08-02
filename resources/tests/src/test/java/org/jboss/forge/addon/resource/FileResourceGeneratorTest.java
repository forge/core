package org.jboss.forge.addon.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;
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
            @AddonDependency(name = "org.jboss.forge.addon:facets", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Test
   public void testCreateUnknownFileResource() throws Exception
   {
      File tempFile = new File(UUID.randomUUID().toString());
      FileResource<?> resource = factory.create(tempFile).reify(FileResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(FileResourceImpl.class, resource.getClass());
   }

   @Test
   public void testCreateFileResource() throws Exception
   {
      File file = File.createTempFile("forge", "testCreateFileResource");
      file.deleteOnExit();
      FileResource<?> resource = factory.create(file).reify(FileResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(FileResourceImpl.class, resource.getClass());
   }

   @Test
   public void testCreateDirectoryResource() throws Exception
   {
      File dir = File.createTempFile("forge", "testCreateDirectoryResource");
      dir.delete();
      dir.mkdir();
      dir.deleteOnExit();

      Resource<File> resource = factory.create(dir);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource.exists());
      Assert.assertTrue(resource instanceof DirectoryResource);
   }

   @Test
   public void testCreateDirectoryResourceViaRiefy() throws Exception
   {
      File dir = File.createTempFile("forge", "testCreateDirectoryResourceViaRiefy");
      dir.delete();
      dir.mkdir();
      dir.deleteOnExit();

      File child = new File(dir, "child");
      child.mkdir();
      child.deleteOnExit();

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
      File file = File.createTempFile("forge", "testFileSize");
      file.deleteOnExit();

      FileOutputStream fos = new FileOutputStream(file);
      Streams.write(new ByteArrayInputStream("Test".getBytes()), fos);
      fos.close();

      FileResource<?> fileResource = factory.create(file).reify(FileResource.class);

      Assert.assertEquals(file.length(), fileResource.getSize());
      file.delete();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testDirectorySize() throws Exception
   {
      File dir = File.createTempFile("forge", "testDirectorySize");
      dir.delete();
      dir.mkdir();
      dir.deleteOnExit();
      factory.create(dir).reify(DirectoryResource.class).getSize();
   }

   @Test
   public void testFileFlags() throws Exception
   {
      File tempFile = File.createTempFile("forge", "testFileFlags");
      tempFile.deleteOnExit();
      FileResource<?> resource = factory.create(tempFile).reify(FileResource.class);

      if (OperatingSystemUtils.isWindows())
         Assert.assertTrue(resource.isExecutable());
      else
         Assert.assertFalse(resource.isExecutable());

      Assert.assertTrue(resource.isReadable());
      Assert.assertTrue(resource.isWritable());
   }

   @Test
   public void testDirectoryFlags() throws Exception
   {
      File file = File.createTempFile("forge", "testDirectoryFlags");
      file.delete();
      DirectoryResource resource = factory.create(file).reify(DirectoryResource.class);
      resource.deleteOnExit();
      resource.mkdir();
      Assert.assertFalse(resource.isExecutable());
      Assert.assertFalse(resource.isReadable());
      Assert.assertFalse(resource.isWritable());
   }

   @Test
   public void testReifyDirectoryResourceFailsIfFileExists() throws Exception
   {
      File tempFile = File.createTempFile("forge", "testReifyDirectoryResourceFailsIfFileExists");
      tempFile.deleteOnExit();
      DirectoryResource reified = factory.create(tempFile).reify(DirectoryResource.class);
      Assert.assertNull(reified);
   }

   @Test
   public void testRenameResource() throws Exception
   {
      File tempFile = File.createTempFile("forge", "testRenameResource");
      tempFile.delete();
      DirectoryResource tempDir = factory.create(tempFile).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Assert.assertNotNull(tempDir);
      FileResource<?> child = tempDir.getChild("testFile").reify(FileResource.class);
      FileResource<?> child2 = tempDir.getChild("testFile2").reify(FileResource.class);
      Assert.assertTrue(child.createNewFile());
      Assert.assertTrue(child.renameTo(child2));
      Assert.assertEquals(child.getFullyQualifiedName(), child2.getFullyQualifiedName());
   }

   @Test
   public void testGetSetContents() throws Exception
   {
      File file = File.createTempFile("forge", "testFileReadWrite");
      file.deleteOnExit();

      FileOutputStream fos = new FileOutputStream(file);
      Streams.write(new ByteArrayInputStream("READ".getBytes()), fos);
      fos.close();

      FileResource<?> fileResource = factory.create(file).reify(FileResource.class);

      Assert.assertEquals("READ", fileResource.getContents());
      fileResource.setContents("WRITE");
      Assert.assertEquals("WRITE", fileResource.getContents());
      file.delete();
   }
   
   @Test
   public void testGetSetContentsWithCharset() throws Exception
   {
      File file = File.createTempFile("forge", "testFileReadWrite");
      file.deleteOnExit();
      Charset charset = Charset.forName("ISO-8859-1");
      
      FileOutputStream fos = new FileOutputStream(file);
      Streams.write(new ByteArrayInputStream("READ".getBytes(charset)), fos);
      fos.close();

      FileResource<?> fileResource = factory.create(file).reify(FileResource.class);

      Assert.assertEquals("READ", fileResource.getContents(charset));
      fileResource.setContents("WRITE",charset);
      Assert.assertEquals("WRITE", fileResource.getContents(charset));
      file.delete();
   }

}