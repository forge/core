package test.org.jboss.forge.parser.java;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaParserService;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JavaParserServiceTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:parser-java", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:parser-java", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private JavaParserService parser;

   @Test
   public void testJavaResourceCreation() throws Exception
   {
      JavaClass javaClass = parser.create(JavaClass.class).setPackage("org.jboss.forge.test").setName("Example");
      Assert.assertEquals(0, javaClass.getMembers().size());
   }
}
