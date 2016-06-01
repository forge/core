/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigurationTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:configuration")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private Configuration configuration;

   @Inject
   @Subset("subset")
   private Configuration subsetConfiguration;

   @Test
   public void testConfigurationInjection() throws Exception
   {
      assertNotNull(configuration);
      assertNotNull(subsetConfiguration);
   }

   @Test
   public void testSubsetConfiguration() throws Exception
   {
      subsetConfiguration.setProperty("A", "Value");
      assertEquals("Value", configuration.subset("subset").getString("A"));
   }

   @Test
   public void testSubsetConfigurationKeys() throws Exception
   {
      subsetConfiguration.clear();
      subsetConfiguration.setProperty("A", "Value");
      Iterator<?> keys = subsetConfiguration.getKeys();
      assertTrue(keys.hasNext());
      assertEquals("A", keys.next());
      assertFalse(keys.hasNext());
   }

   @Test
   public void testSubsetConfigurationClearProperty() throws Exception
   {
      subsetConfiguration.clear();
      subsetConfiguration.setProperty("A", "Value");
      assertTrue(subsetConfiguration.getKeys().hasNext());
      subsetConfiguration.clearProperty("A");
      assertFalse(subsetConfiguration.getKeys().hasNext());
   }

}