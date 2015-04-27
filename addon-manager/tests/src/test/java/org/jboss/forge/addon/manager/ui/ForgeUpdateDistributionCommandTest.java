package org.jboss.forge.addon.manager.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.lingala.zip4j.core.ZipFile;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@RunWith(Arquillian.class)
public class ForgeUpdateDistributionCommandTest
{
   private static String FORGE_OLD_VERSION = "2.0.0.Final";

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsResource(ForgeUpdateDistributionCommandTest.class.getResource(TEST_OLD_FORGE), TEST_OLD_FORGE);
      return archive;
   }

   private String forgeHome;
   private File tempDir;

   private static String TEST_OLD_FORGE = "/forge-old-distribution.zip";

   @Inject
   private Instance<CommandProvider> extensions;

   @Inject
   private Addon addon;

   @Inject
   private Furnace furnace;

   @Inject
   private UITestHarness uiTestHarness;

   @Before
   public void testInitialization() throws Exception
   {
      tempDir = OperatingSystemUtils.createTempDir();
      disablePreviousAddonCommands(furnace);
      File inputFile = File.createTempFile("forge-old-distribution", ".zip", tempDir);
      inputFile.deleteOnExit();
      try (InputStream iStream = getClass().getResourceAsStream(TEST_OLD_FORGE))
      {
         try (OutputStream oStream = new FileOutputStream(inputFile))
         {
            Streams.write(iStream, oStream);
         }
      }

      ZipFile zipFile = new ZipFile(inputFile.getAbsolutePath());
      String extractedFolderPath = tempDir.getAbsolutePath() + "/extracted-forge";
      new File(extractedFolderPath).mkdirs();
      zipFile.extractAll(extractedFolderPath);
      forgeHome = extractedFolderPath + "/forge-old-distribution";
      System.setProperty("forge.home", forgeHome);
      File homeAddonsDir = new File(forgeHome + "/addons");
      Assert.assertTrue(homeAddonsDir.exists());
      Assert.assertTrue(homeAddonsDir.listFiles().length == 0);
      String uiPreviousVersion = getPreviousVersion(addon.getRepository().getRootDirectory().getPath());
      //TODO: After introducing new DistributionCommand, changeManagerAddonDirectoryToBeOlder needs adjustment (it changed installed.xml and directory)
      changeManagerAddonDirectoryToBeOlder(addon.getRepository().getRootDirectory(), uiPreviousVersion);
      waitForAddonManagerAddon(furnace);
      do
      {
         // we need to wait for the command to be accessible
         Thread.sleep(500);
      }
      while (furnace.getAddonRegistry().getServices("org.jboss.forge.addon.manager.impl.ui.DistributionCommand")
               .isUnsatisfied());
      uiTestHarness.setGui(false);
   }

   @Test
   public void testUpdateDistributionCommand1() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController("Forge: Update"))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         Assert.assertFalse(new File(forgeHome + "/.update").exists());
         Result result = controller.execute();
         Assert.assertFalse("Forge Update Distribution command should succeed, but it failed.",
                  result instanceof Failed);
         Assert.assertTrue(
                  ".update folder should have been placed in WINDUP_HOME/.update already, but it is not there.",
                  new File(forgeHome + "/.update").exists());
         File addonsHomeNew = new File(forgeHome + "/.update/addons");
         File binNew = new File(forgeHome + "/.update/bin");
         File libNew = new File(forgeHome + "/.update/lib");
         Assert.assertTrue(".update/addons folder was not updated sucessfully", addonsHomeNew.exists());
         Assert.assertTrue(".update/bin folder was not updated sucessfully", binNew.exists());
         Assert.assertTrue(".update/lib folder was not updated sucessfully", libNew.exists());
         Assert.assertTrue(".update/bin folder does not contain enough items (at least 2)",
                  binNew.listFiles().length > 1);
         Assert.assertTrue(".update/lib folder does not contain enough libraries (at least 8)",
                  libNew.listFiles().length > 7);
         Assert.assertTrue(".update/addons folder does not contain enough addons (at least 6)",
                  addonsHomeNew.listFiles().length > 5);
      }
      try (CommandController controller = uiTestHarness.createCommandController("Forge: Cancel Update Distribution"))
      {
         try
         {
            controller.initialize();
            Assert.assertTrue(controller.isEnabled());
            Assert.assertTrue(new File(forgeHome + "/.update").exists());
            Result result = controller.execute();
            Assert.assertFalse("Forge Cancel Update Distribution command should succeed, but it failed.",
                     result instanceof Failed);
            Assert.assertFalse(
                     ".update folder should have been removed in WINDUP_HOME/.update",
                     new File(forgeHome + "/.update").exists());
         }
         finally
         {
            deleteWholeDirectory(tempDir);
         }
      }
   }


   private void disablePreviousAddonCommands(Furnace furnace2)
   {
      Set<Addon> addons = furnace.getAddonRegistry().getAddons();
      for (Addon addon : addons)
      {
         if (addon.getId().getName().contains("addon-manager") && !addon.getId().getName().contains("addon-manager-spi"))
         {
            // we have outdated addon-manager addon
            for(CommandProvider cp : extensions)
               cp.addonUndeployed(addon.getId());
         }
      }
   }

   private void waitForAddonManagerAddon(Furnace furnace)
   {
      boolean addonManagerNotStarted = true;
      while (addonManagerNotStarted)
      {
         Set<Addon> addons = furnace.getAddonRegistry().getAddons();
         for (Addon addon : addons)
         {
            if (addon.getId().getVersion().toString().equals(FORGE_OLD_VERSION)
                     && addon.getId().getName().contains("addon-manager"))
            {
               while (!addon.getStatus().isStarted())
               {
                  try
                  {
                     Thread.sleep(100);
                  }
                  catch (InterruptedException e)
                  {
                     e.printStackTrace();
                  }
               }
               addonManagerNotStarted = false;
               break;
            }
         }
      }

   }

   private void changeManagerAddonDirectoryToBeOlder(File homeAddonsDir, String uiPreviousVersion) throws IOException,
            TransformerException, SAXException
   {
      final File uiAddonDirectory = new File(homeAddonsDir.getAbsolutePath() + "/org-jboss-forge-addon-addon-manager-"
               + uiPreviousVersion.replaceAll("\\.", "-"));
      final File olderVersionAddon = new File(homeAddonsDir.getAbsolutePath() + "/org-jboss-forge-addon-addon-manager-"
               + FORGE_OLD_VERSION.replaceAll("\\.", "-"));
      if (!olderVersionAddon.exists())
      {
         olderVersionAddon.mkdir();
      }
      Files.walkFileTree(uiAddonDirectory.toPath(), new SimpleFileVisitor<Path>()
      {
         @Override
         public FileVisitResult preVisitDirectory(final Path dir,
                  final BasicFileAttributes attrs) throws IOException
         {
            Files.createDirectories(olderVersionAddon.toPath().resolve(uiAddonDirectory.toPath()
                     .relativize(dir)));
            return FileVisitResult.CONTINUE;
         }

         @Override
         public FileVisitResult visitFile(final Path file,
                  final BasicFileAttributes attrs) throws IOException
         {
            Files.copy(file,
                     olderVersionAddon.toPath().resolve(uiAddonDirectory.toPath().relativize(file)));
            return FileVisitResult.CONTINUE;
         }
      });

      makeManagerAddonVersionOlder(homeAddonsDir.getPath());
      deleteWholeDirectory(uiAddonDirectory);
   }

   private String getPreviousVersion(String homeAddonsDirPath) throws IOException, SAXException
   {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder;
      try
      {
         dBuilder = dbFactory.newDocumentBuilder();
         String oldUiAddonVersion = "";
         File installedXml = new File(homeAddonsDirPath + "/installed.xml");
         Document doc = dBuilder.parse(installedXml);
         Element documentElement = doc.getDocumentElement();
         NodeList childNodes = documentElement.getElementsByTagName("addon");
         for (int i = 0; i <= childNodes.getLength() - 1; i++)
         {
            Element item = (Element) childNodes.item(i);
            if (item.getNodeName().equals("addon"))
            {
               String addonName = item.getAttribute("name");
               if (addonName.equals("org.jboss.forge.addon:addon-manager"))
               {
                  oldUiAddonVersion = item.getAttribute("version");
                  return oldUiAddonVersion;
               }
            }
         }
      }
      catch (ParserConfigurationException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * This method changes the installed.xml so it has outdated addon-manager
    * 
    * @param homeAddonsDirPath
    * @throws TransformerException
    */
   private void makeManagerAddonVersionOlder(String homeAddonsDirPath) throws TransformerException, SAXException,
            IOException
   {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder;
      try
      {
         dBuilder = dbFactory.newDocumentBuilder();
         File installedXml = new File(homeAddonsDirPath + "/installed.xml");
         Document doc = dBuilder.parse(installedXml);
         // this does not work properly
         Element documentElement = doc.getDocumentElement();
         NodeList childNodes = documentElement.getElementsByTagName("addon");
         for (int i = 0; i <= childNodes.getLength() - 1; i++)
         {
            Element item = (Element) childNodes.item(i);
            if (item.getNodeName().equals("addon"))
            {
               String addonName = item.getAttribute("name");
               if (addonName.equals("org.jboss.forge.addon:addon-manager"))
               {
                  item.setAttribute("version", FORGE_OLD_VERSION);
                  TransformerFactory transformerFactory = TransformerFactory.newInstance();
                  Transformer transformer = transformerFactory.newTransformer();
                  DOMSource source = new DOMSource(doc);
                  StreamResult result = new StreamResult(installedXml);
                  transformer.transform(source, result);
                  return;
               }
            }
         }
      }
      catch (ParserConfigurationException e)
      {
         e.printStackTrace();
      }
      return;
   }

   private void deleteWholeDirectory(File directory) throws IOException
   {
      Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>()
      {
         @Override
         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                  throws IOException
         {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
         }

         @Override
         public FileVisitResult postVisitDirectory(Path dir,
                  IOException exc) throws IOException
         {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
         }
      });
   }

}