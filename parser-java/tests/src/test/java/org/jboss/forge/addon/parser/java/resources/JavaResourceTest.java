/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.resources;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
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
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/parser/java/resources/MyClass.java")),
                        "org/jboss/forge/addon/parser/java/resources/MyClass.java")
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources")
               );
      return archive;
   }

   @Inject
   private ResourceFactory resourceFactory;

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
}
