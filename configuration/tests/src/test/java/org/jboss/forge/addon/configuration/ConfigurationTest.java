/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigurationTest
{

   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge.addon:maven", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:configuration", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven",
                                 "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects",
                                 "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:configuration",
                                 "2.0.0-SNAPSHOT")
               );
      return archive;
   }

   @Inject
   private Configuration configuration;

   @Inject
   private ProjectFactory projectFactory;

   @Test
   public void testConfigurationInjection() throws Exception
   {
      assertNotNull(configuration);
   }

   @Test
   public void testProjectFacet() throws Exception
   {
      Project project = projectFactory.createTempProject();
      assertTrue(project.hasFacet(ConfigurationFacet.class));
      ConfigurationFacet facet = project.getFacet(ConfigurationFacet.class);
      assertFalse(facet.getConfigLocation().exists());
      Configuration config = facet.getConfiguration();
      config.setProperty("key", "value");
      assertEquals("value", config.getString("key"));
      assertTrue(facet.getConfigLocation().exists());
   }
}