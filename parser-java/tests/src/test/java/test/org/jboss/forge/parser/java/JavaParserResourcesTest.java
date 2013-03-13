package test.org.jboss.forge.parser.java;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.resources.JavaResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JavaParserResourcesTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:resources", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:parser-java", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:parser-java", "2.0.0-SNAPSHOT")),
                        AddonDependency.create(AddonId.from("org.jboss.forge:resources", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Test
   public void testJavaResourceCreation() throws Exception
   {
      JavaClass javaClass = JavaParser.create(JavaClass.class).setPackage("org.jboss.forge.test").setName("Example");
      JavaResource resource = factory.create(JavaResource.class, File.createTempFile("forge", ".java"));
      resource.createNewFile();
      resource.setContents(javaClass);

      Assert.assertEquals("Example", resource.getJavaSource().getName());
      Assert.assertEquals("org.jboss.forge.test", resource.getJavaSource().getPackage());
   }
}
