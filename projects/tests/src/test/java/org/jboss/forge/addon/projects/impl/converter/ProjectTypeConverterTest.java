/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.converter;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectTypeConverterTest
{

   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"),      
            @Addon(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClass(TestProjectType.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")),                        
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:projects", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private Converter<String, ProjectType> converter;

   @Test
   public void testConverterInjection() throws Exception
   {
      Assert.assertNotNull(converter);
   }

   @Test
   public void testConversionForTestProjectType() throws Exception
   {
      ProjectType type = converter.convert("test");
      Assert.assertThat(type, is(instanceOf(TestProjectType.class)));
   }
}
