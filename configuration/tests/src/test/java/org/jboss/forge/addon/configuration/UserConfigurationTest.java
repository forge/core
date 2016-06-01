/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UserConfigurationTest
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
   private Configuration userConfiguration;

   @Test
   public void testUserConfiguration()
   {
      userConfiguration.setProperty("key", "value");
      Assert.assertEquals("value", userConfiguration.getString("key"));
   }

   @Test
   public void testUserConfigurationSubset()
   {
      Configuration subset = userConfiguration.subset("subset-1");
      subset.setProperty("key", "value");
      Assert.assertEquals("value", userConfiguration.subset("subset-1").getString("key"));
      subset.setProperty("another-key", 123L);
      Assert.assertEquals(123L, userConfiguration.subset("subset-1").getLong("another-key"));
   }
}