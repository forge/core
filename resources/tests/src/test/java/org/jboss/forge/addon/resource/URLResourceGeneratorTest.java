/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.net.URL;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.URLResource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class URLResourceGeneratorTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:facets", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Test
   public void testCreateURLResource() throws Exception
   {
      URL url = new URL("http://forge.jboss.org");
      Resource<?> resource = factory.create(url);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource instanceof URLResource);
      Assert.assertSame(url, resource.getUnderlyingResourceObject());
   }

}