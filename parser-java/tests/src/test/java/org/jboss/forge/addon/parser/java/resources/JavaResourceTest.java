/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.resources;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.util.FormatterProfileReader;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JavaResourceTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/parser/java/resources/MyClass.java")),
                        "org/jboss/forge/addon/parser/java/resources/MyClass.java")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/parser/java/resources/forge_profile.xml")),
                        "org/jboss/forge/addon/parser/java/resources/forge_profile.xml")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/parser/java/resources/eclipse_profile.xml")),
                        "org/jboss/forge/addon/parser/java/resources/eclipse_profile.xml")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/parser/java/resources/formatter_eclipse.jv")),
                        "org/jboss/forge/addon/parser/java/resources/formatter_eclipse.jv")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/parser/java/resources/formatter_forge.jv")),
                        "org/jboss/forge/addon/parser/java/resources/formatter_forge.jv")
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:configuration"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources")
               );
      return archive;
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private Configuration configuration;

   @Test
   public void testParserClass() throws Exception
   {
      File tmpFile = File.createTempFile("MyClass", ".java");
      tmpFile.deleteOnExit();
      Resource<File> unreified = resourceFactory.create(tmpFile);
      JavaResource resource = unreified.reify(JavaResource.class);
      Assert.assertNotNull(resource);

      resource.setContents(getClass().getResource("MyClass.java").openStream());

      // Testing java-parser
      JavaSource<?> javaSource = resource.getJavaType();
      Assert.assertThat(javaSource, instanceOf(JavaClass.class));

      // Testing roaster
      JavaType<?> javaType = resource.getJavaType();
      Assert.assertThat(javaType, instanceOf(JavaClassSource.class));
   }

   @Test
   public void testDefaultClassFormatting() throws Exception
   {
      configuration.clearProperty(JavaResource.FORMATTER_PROFILE_PATH_KEY);
      String forgeFormatterContents = Streams.toString(getClass().getResourceAsStream("formatter_forge.jv"));
      String eclipseFormatterContents = Streams.toString(getClass().getResourceAsStream("formatter_eclipse.jv"));
      File tmpFile = File.createTempFile("MyClass", ".java");
      tmpFile.deleteOnExit();
      JavaResource resource = resourceFactory.create(JavaResource.class, tmpFile);
      resource.setContents(forgeFormatterContents);
      Assert.assertEquals(forgeFormatterContents, resource.getContents());
      File profileFile = File.createTempFile("profile", ".xml");
      try (InputStream is = getClass().getResourceAsStream("eclipse_profile.xml"))
      {
         Files.copy(is, profileFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
      configuration.setProperty(JavaResource.FORMATTER_PROFILE_PATH_KEY, profileFile.getAbsolutePath());
      resource = resourceFactory.create(JavaResource.class, tmpFile);
      resource.setContents(forgeFormatterContents);
      Assert.assertEquals(eclipseFormatterContents, resource.getContents());
   }

   @Test
   public void testCustomClassFormattingProperties() throws Exception
   {
      configuration.clearProperty(JavaResource.FORMATTER_PROFILE_PATH_KEY);
      String forgeFormatterContents = Streams.toString(getClass().getResourceAsStream("formatter_forge.jv"));
      File tmpFile = File.createTempFile("MyClass", ".java");
      tmpFile.deleteOnExit();
      JavaResource resource = resourceFactory.create(JavaResource.class, tmpFile);
      resource.setContents(forgeFormatterContents);
      Assert.assertEquals(forgeFormatterContents, resource.getContents());
      File profileFile = File.createTempFile("profile", ".xml");
      try (InputStream is = getClass().getResourceAsStream("eclipse_profile.xml"))
      {
         Files.copy(is, profileFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
      Properties properties = null;
      try (InputStream is = getClass().getResourceAsStream("forge_profile.xml"))
      {
         FormatterProfileReader reader = FormatterProfileReader.fromEclipseXml(is);
         properties = reader.getPropertiesFor("Forge");
      }
      configuration.setProperty(JavaResource.FORMATTER_PROFILE_PATH_KEY, profileFile.getAbsolutePath());
      resource = resourceFactory.create(JavaResource.class, tmpFile);
      resource.setContents(new ByteArrayInputStream(forgeFormatterContents.getBytes()), properties);
      Assert.assertEquals(forgeFormatterContents, resource.getContents());
   }

}
