/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for interacting with {@link Version} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Versions
{
   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.|-)(.*)");

   /**
    * This method only returns true if:
    * 
    * - The major version of addonApiVersion is equal to the major version of runtimeVersion AND
    * 
    * - The minor version of addonApiVersion is less or equal to the minor version of runtimeVersion
    * 
    * - The addonApiVersion is null
    * 
    * @param runtimeVersion a version in the format x.x.x
    * @param addonApiVersion a version in the format x.x.x
    */
   public static boolean isApiCompatible(Version runtimeVersion, Version addonApiVersion)
   {
      if (addonApiVersion == null || addonApiVersion.getVersionString().length() == 0
               || runtimeVersion == null || runtimeVersion.getVersionString().length() == 0)
         return true;

      Matcher runtimeMatcher = VERSION_PATTERN.matcher(runtimeVersion.getVersionString());
      if (runtimeMatcher.matches())
      {
         int runtimeMajorVersion = Integer.parseInt(runtimeMatcher.group(1));
         int runtimeMinorVersion = Integer.parseInt(runtimeMatcher.group(2));

         Matcher addonApiMatcher = VERSION_PATTERN.matcher(addonApiVersion.getVersionString());
         if (addonApiMatcher.matches())
         {
            int addonApiMajorVersion = Integer.parseInt(addonApiMatcher.group(1));
            int addonApiMinorVersion = Integer.parseInt(addonApiMatcher.group(2));

            if (addonApiMajorVersion == runtimeMajorVersion && addonApiMinorVersion <= runtimeMinorVersion)
            {
               return true;
            }
         }
      }
      return false;
   }

   public static boolean areEqual(Version left, Version right)
   {
      if (left == right)
         return true;
      if (left == null || right == null)
         return false;
      if (left.getVersionString().equals(right.getVersionString()))
         return true;

      return false;
   }
}
