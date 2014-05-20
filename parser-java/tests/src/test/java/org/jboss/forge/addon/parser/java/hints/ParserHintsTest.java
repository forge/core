/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.hints;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.ui.hints.HintsLookup;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ParserHintsTest
{
   @Deployment
   @Dependencies({ 
      @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-spi"),
            @AddonDependency(name = "org.jboss.forge.addon:environment"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:parser-java") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-spi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:environment")
               );

      return archive;
   }

   @Inject
   private Environment environment;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(environment);
   }

   @Test
   public void testSimpleHintLookup() throws Exception
   {
      HintsLookup hints = new HintsLookup(environment);
      String type = hints.getInputType(JavaResource.class);
      Assert.assertNotNull(type);
      Assert.assertEquals(InputType.JAVA_CLASS_PICKER, type);
   }
}
