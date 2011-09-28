/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

   @Test(expected = IllegalArgumentException.class)
   public void testAppendToArray3()
   {
      String[] foo = null;
      String[] bar = new String[] { "3", "4" };

      GeneralUtils.join(String.class, foo, bar);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testAppendToArray4()
   {
      String[] foo = new String[] { "3", "4" };
      String[] bar = null;

      GeneralUtils.join(String.class, foo, bar);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testAppendToArray5()
   {
      String[] foo = new String[] { "3", "4" };
      String[] bar = new String[] { "3", "4" };

      GeneralUtils.join(null, foo, bar);
   }

}
