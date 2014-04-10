package org.jboss.forge.addon.resource;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * Refactor of FileResourceGeneratorTest but for {@link Path}s
 *
 * @author Shane Bryzak
 *
 */
@RunWith(Arquillian.class)
public class PathResourceGeneratorTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:facets"),
            @AddonDependency(name = "org.jboss.forge.addon:resources") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:facets"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"));

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Test
   public void testCreateUnknownPathResource() throws Exception
   {
      Path tempFile = FileSystems.getDefault().getPath(UUID.randomUUID().toString());
      tempFile = Files.createFile(tempFile);
      tempFile.toFile().deleteOnExit();
      PathResource resource = factory.create(tempFile).reify(PathResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(PathResourceImpl.class, resource.getClass());
   }

   @Test
   public void testCreatePathResource() throws Exception
   {
      Path file = Files.createTempFile("forge", "testCreateFileResource");
      file.toFile().deleteOnExit();
      PathResource resource = factory.create(file).reify(PathResource.class);
      Assert.assertNotNull(resource);
      Assert.assertEquals(PathResourceImpl.class, resource.getClass());
   }

   @Test
   public void testCreateDirectoryResource() throws Exception
   {
      Path dir = Files.createTempFile("forge", "testCreateDirectoryResource");
      Files.delete(dir);
      Files.createDirectory(dir);
      dir.toFile().deleteOnExit();

      Resource<Path> resource = factory.create(dir);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource.exists());
      Assert.assertTrue(resource instanceof PathResource);
   }

   @Test
   public void testCreateDirectoryResourceViaRiefy() throws Exception
   {
      Path dir = Files.createTempFile("forge", "testCreateDirectoryResourceViaRiefy");
      Files.delete(dir);
      Files.createDirectory(dir);
      dir.toFile().deleteOnExit();

      Path child = dir.resolve("child");
      Files.createDirectory(child);
      child.toFile().deleteOnExit();

      PathResource resource = factory.create(dir).reify(PathResource.class);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource.exists());

      PathResource childResource = resource.getChild("child").reify(PathResource.class);
      Assert.assertNotNull(childResource);
      Assert.assertTrue(childResource.exists());
   }

   @Test
   public void testFileSize() throws Exception
   {
      Path file = Files.createTempFile("forge", "testFileSize");
      file.toFile().deleteOnExit();

      OutputStream fos = Files.newOutputStream(file);
      Streams.write(new ByteArrayInputStream("Test".getBytes()), fos);
      fos.close();

      PathResource fileResource = factory.create(file).reify(PathResource.class);

      Assert.assertEquals(Files.size(file), fileResource.getSize());
      Files.delete(file);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testDirectorySize() throws Exception
   {
      Path dir = Files.createTempFile("forge", "testDirectorySize");
      Files.delete(dir);
      Files.createDirectory(dir);
      dir.toFile().deleteOnExit();
      factory.create(dir).reify(PathResource.class).getSize();
   }

   @Test
   public void testFileFlags() throws Exception
   {
      Path tempFile = Files.createTempFile("forge", "testFileFlags");
      tempFile.toFile().deleteOnExit();
      PathResource resource = factory.create(tempFile).reify(PathResource.class);

      if (OperatingSystemUtils.isWindows())
         Assert.assertTrue(resource.isExecutable());
      else
         Assert.assertFalse(resource.isExecutable());

      Assert.assertTrue(resource.isReadable());
      Assert.assertTrue(resource.isWritable());
   }

   @Test
   public void testRenameResource() throws Exception
   {
      Path tempFile = OperatingSystemUtils.createTempDir().toPath();
      PathResource tempDir = factory.create(tempFile).reify(PathResource.class);
      tempDir.deleteOnExit();

      Assert.assertNotNull(tempDir);
      PathResource child = tempDir.getChild("testFile").reify(PathResource.class);
      PathResource child2 = tempDir.getChild("testFile2").reify(PathResource.class);
      Assert.assertTrue(child.createNewPath());
      Assert.assertTrue(child.renameTo(child2));
      Assert.assertEquals(child.getFullyQualifiedName(), child2.getFullyQualifiedName());
   }

   @Test
   public void testGetSetContents() throws Exception
   {
      Path file = Files.createTempFile("forge", "testFileReadWrite");
      file.toFile().deleteOnExit();

      OutputStream fos = Files.newOutputStream(file);
      Streams.write(new ByteArrayInputStream("READ".getBytes()), fos);
      fos.close();

      PathResource fileResource = factory.create(file).reify(PathResource.class);

      Assert.assertEquals("READ", fileResource.getContents());
      fileResource.setContents("WRITE");
      Assert.assertEquals("WRITE", fileResource.getContents());
      Files.delete(file);
   }

   @Test
   public void testGetSetContentsWithCharset() throws Exception
   {
      Path file = Files.createTempFile("forge", "testFileReadWrite");
      file.toFile().deleteOnExit();
      Charset charset = Charset.forName("ISO-8859-1");

      OutputStream fos = Files.newOutputStream(file);
      Streams.write(new ByteArrayInputStream("READ".getBytes(charset)), fos);
      fos.close();

      PathResource fileResource = factory.create(file).reify(PathResource.class);

      Assert.assertEquals("READ", fileResource.getContents(charset));
      fileResource.setContents("WRITE", charset);
      Assert.assertEquals("WRITE", fileResource.getContents(charset));
      Files.delete(file);
   }

}