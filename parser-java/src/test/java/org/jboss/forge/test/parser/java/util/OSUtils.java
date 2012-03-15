/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.test.parser.java.util;

import java.io.File;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class OSUtils
{
   private static boolean PRETEND_WINDOWS = Boolean.getBoolean("forge.pretend_windows");

   private static String operatingSystem = null;

   public static String getOsName()
   {
      if (operatingSystem == null)
      {
         operatingSystem = System.getProperty("os.name");
      }
      return operatingSystem;
   }

   public static boolean isWindows()
   {
      return PRETEND_WINDOWS || getOsName().startsWith("Windows") || getOsName().startsWith("windows");
   }

   public static boolean isOSX()
   {
      return getOsName().startsWith("Mac") || getOsName().startsWith("mac");
   }

   public static boolean isLinux()
   {
      return getOsName().startsWith("Linux") || getOsName().startsWith("linux");
   }

   public static File getUserHomeDir()
   {
      return new File(System.getProperty("user.home")).getAbsoluteFile();
   }

   public static String getUserHomePath()
   {
      return getUserHomeDir().getAbsolutePath();
   }

   public static void setPretendWindows(boolean value)
   {
      PRETEND_WINDOWS = value;
   }

   public static String getLineSeparator()
   {
      return System.getProperty("line.separator");
   }
}
