/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.net.URL;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class URLResourceGeneratorTest
{
   private ResourceFactory factory;

   @Before
   public void setUp()
   {
      this.factory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testCreateURLResource() throws Exception
   {
      URL url = new URL("https://forge.jboss.org");
      Resource<?> resource = factory.create(url);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource instanceof URLResource);
      Assert.assertSame(url, resource.getUnderlyingResourceObject());
   }

   @Test
   public void testCreateURLResourceFromString() throws Exception
   {
      String url = "https://forge.jboss.org";
      Resource<?> resource = factory.create(url);
      Assert.assertNotNull(resource);
      Assert.assertTrue(resource instanceof URLResource);
      Assert.assertEquals(new URL(url), resource.getUnderlyingResourceObject());
   }

   @Test
   public void testCreateURLResourceFromInvalidString() throws Exception
   {
      String url = "adsfadsfsadfsdfads";
      Resource<?> resource = factory.create(url);
      Assert.assertNull(resource);
   }

}