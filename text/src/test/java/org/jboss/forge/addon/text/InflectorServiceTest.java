/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InflectorServiceTest
{
   private Inflector inflector;

   @Before
   public void setUp()
   {
      inflector = SimpleContainer.getServices(getClass().getClassLoader(), Inflector.class).get();
   }

   @Test
   public void testInflectorInjection() throws Exception
   {
      Assert.assertNotNull(inflector);
      Assert.assertEquals("Forge", inflector.capitalize("forGE"));
   }

}