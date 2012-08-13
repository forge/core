/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.util;

import static org.junit.Assert.assertEquals;

import org.jboss.forge.shell.util.GeneralUtils;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class GeneralUtilsTest
{

   @Test
   public void testAppendToArray()
   {
      String[] foo = new String[] { "1", "2" };
      String[] bar = new String[] { "3", "4" };

      String[] result = GeneralUtils.join(String.class, foo, bar);
      assertEquals("1", result[0]);
      assertEquals("2", result[1]);
      assertEquals("3", result[2]);
      assertEquals("4", result[3]);
   }

   @Test
   public void testAppendToArray2()
   {
      String[] foo = new String[] {};
      String[] bar = new String[] { "3", "4" };

      String[] result = GeneralUtils.join(String.class, foo, bar);
      assertEquals("3", result[0]);
      assertEquals("4", result[1]);
   }

   @Test(expected = IllegalStateException.class)
   public void testAppendToArray3()
   {
      String[] foo = null;
      String[] bar = new String[] { "3", "4" };

      GeneralUtils.join(String.class, foo, bar);
   }

   @Test(expected = IllegalStateException.class)
   public void testAppendToArray4()
   {
      String[] foo = new String[] { "3", "4" };
      String[] bar = null;

      GeneralUtils.join(String.class, foo, bar);
   }

   @Test(expected = IllegalStateException.class)
   public void testAppendToArray5()
   {
      String[] foo = new String[] { "3", "4" };
      String[] bar = new String[] { "3", "4" };

      GeneralUtils.join(null, foo, bar);
   }

}
