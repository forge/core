/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.devtools.java;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.parser.xml.resources.XMLResource;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JavaFormatSourcesCommandTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {

      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/devtools/java/resources/FormattedSource.java")),
                        "org/jboss/forge/addon/devtools/java/FormattedSource.java")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/devtools/java/resources/UnformattedSource.java")),
                        "org/jboss/forge/addon/devtools/java/UnformattedSource.java")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/devtools/java/resources/DefaultFormattedSource.java")),
                        "org/jboss/forge/addon/devtools/java/DefaultFormattedSource.java")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/devtools/java/resources/eclipse_profile.xml")),
                        "org/jboss/forge/addon/devtools/java/eclipse_profile.xml")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/devtools/java/resources/forge_profile.xml")),
                        "org/jboss/forge/addon/devtools/java/forge_profile.xml");
      return archive;
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private ShellTest shellTest;

   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      commandController = testHarness.createCommandController(JavaFormatSourcesCommand.class);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   // Runs the test on the command specifying the format sources and the format profile.
   @Test
   public void testFileFormattingWithProfileAndSourceInput() throws Exception
   {

      File formattedFile = File.createTempFile("FormattedSource", ".java");
      formattedFile.deleteOnExit();
      File unformattedFile = File.createTempFile("UnformattedSource", ".java");
      unformattedFile.deleteOnExit();
      File profile = File.createTempFile("FormatProfile", ".xml");
      profile.deleteOnExit();

      // Loading the sample formatted source
      Resource<File> unreifiedFormattedSource = resourceFactory.create(formattedFile);
      FileResource<?> formattedSource = unreifiedFormattedSource.reify(FileResource.class);
      Assert.assertNotNull(getClass().getResource("FormattedSource.java").openStream());
      formattedSource.setContents((getClass().getResource("FormattedSource.java").openStream()));

      // Loading the temporary unformatted source from the sample unformatted source
      Resource<File> unreifiedTempUnformattedSource = resourceFactory.create(unformattedFile);
      FileResource<?> tempUnformattedSource = unreifiedTempUnformattedSource.reify(FileResource.class);
      Assert.assertNotNull(getClass().getResource("UnformattedSource.java").openStream());
      tempUnformattedSource.setContents((getClass().getResource("UnformattedSource.java").openStream()));

      // Loading the sample format profile
      Resource<File> unreifiedProfile = resourceFactory.create(profile);
      XMLResource profileXML = unreifiedProfile.reify(XMLResource.class);
      Assert.assertNotNull(getClass().getResource("eclipse_profile.xml").openStream());
      profileXML.setContents((getClass().getResource("eclipse_profile.xml").openStream()));

      commandController.initialize();
      commandController.setValueFor("profilepath", profileXML);
      commandController.setValueFor("sources", tempUnformattedSource);
      commandController.setValueFor("profilename", "Eclipse [built-in]");

      commandController.execute();

      Assert.assertEquals(formattedSource.getContents(), tempUnformattedSource.getContents());
   }

   // Runs the test on the command without specifying the format sources(formats a directory).
   @SuppressWarnings("unchecked")
   @Test
   public void testFileFormattingWithoutSourceInput() throws Exception
   {
      File formattedFile = File.createTempFile("DefaultFormattedSource", ".java");
      formattedFile.deleteOnExit();

      // Loading the sample formatted source
      Resource<File> unreifiedFormattedSource = resourceFactory.create(formattedFile);
      FileResource<?> formattedSource = unreifiedFormattedSource.reify(FileResource.class);
      Assert.assertNotNull(getClass().getResource("DefaultFormattedSource.java").openStream());
      formattedSource.setContents((getClass().getResource("DefaultFormattedSource.java").openStream()));

      // Creating the temporary directory to be formatted.
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempUnformattedResourceDir = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();
      FileResource<?> tempUnformattedSource = tempUnformattedResourceDir.getChildOfType(FileResource.class,
               "UnformattedFile.java");
      tempUnformattedSource.createNewFile();
      tempUnformattedSource.deleteOnExit();

      // Creating the temporary profile xml.
      FileResource<?> profileXML = tempUnformattedResourceDir.getChildOfType(XMLResource.class,
               "forge_profile.xml");
      profileXML.createNewFile();
      profileXML.deleteOnExit();
      profileXML.setContents((getClass().getResource("forge_profile.xml").openStream()));

      // Setting the contents of the temporary directory to unformatted source.
      Assert.assertNotNull(getClass().getResource("UnformattedSource.java").openStream());
      tempUnformattedSource.setContents((getClass().getResource("UnformattedSource.java").openStream()));

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempUnformattedResourceDir);

      Result javaformatsourcesResult = shellTest.execute(
               "java-format-sources --profilepath forge_profile.xml --profilename Forge", 10,
               TimeUnit.SECONDS);
      Assert.assertNotNull(javaformatsourcesResult);

      // Asserting the contents of the temporary directory are formatted after command execution.
      Assert.assertEquals(formattedSource.getContents(), tempUnformattedSource.getContents());

   }

}