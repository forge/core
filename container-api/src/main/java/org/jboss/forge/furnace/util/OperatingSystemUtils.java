/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

import java.io.File;

/**
 * Utility for dealing with the inconsistencies between common operating systems.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class OperatingSystemUtils
{
   private static boolean PRETEND_WINDOWS = Boolean.getBoolean("forge.pretend_windows");

   private static String operatingSystem = null;

   /**
    * Return the name of the host operating system.
    */
   public static String getOsName()
   {
      if (operatingSystem == null)
      {
         operatingSystem = System.getProperty("os.name");
      }
      return operatingSystem;
   }

   /**
    * Return <code>true</code> if the host environment is Windows.
    */
   public static boolean isWindows()
   {
      return PRETEND_WINDOWS || getOsName().startsWith("Windows") || getOsName().startsWith("windows");
   }

   /**
    * Return <code>true</code> if the host environment is OSX.
    */
   public static boolean isOSX()
   {
      return getOsName().startsWith("Mac") || getOsName().startsWith("mac");
   }

   /**
    * Return <code>true</code> if the host environment is Linux.
    */
   public static boolean isLinux()
   {
      return getOsName().startsWith("Linux") || getOsName().startsWith("linux");
   }

   /**
    * Get the FORGE_HOME directory as a {@link File}.
    */
   public static File getForgeHomeDir()
   {
      return new File(System.getProperty("forge.home")).getAbsoluteFile();
   }

   /**
    * Get the logged in user's home directory as a {@link File}.
    */
   public static File getUserHomeDir()
   {
      return new File(System.getProperty("user.home")).getAbsoluteFile();
   }

   /**
    * Get the path of the logged in user's home directory.
    */
   public static String getUserHomePath()
   {
      return getUserHomeDir().getAbsolutePath();
   }

   /**
    * Get the logged in user's Furnace directory.
    */
   public static File getUserForgeDir()
   {
      return new File(getUserHomeDir(), ".forge").getAbsoluteFile();
   }

   /**
    * Set this utility to pretend that the current host environment is actually Windows.
    */
   public static void setPretendWindows(boolean value)
   {
      PRETEND_WINDOWS = value;
   }

   /**
    * Return the {@link String} used as line separator for carriage returns.
    */
   public static String getLineSeparator()
   {
      return System.getProperty("line.separator");
   }

   /**
    * Generate a string that is usable as a single file name or directory path segment on any operating system. Replaces
    * unsafe characters with the underscore '_' character
    */
   public static String getSafeFilename(String filename)
   {
      String result = filename;
      if (result != null)
      {
         result = result.replaceAll("[/?<>\\\\:*|\"]", "_");
      }
      return result;
   }
}
