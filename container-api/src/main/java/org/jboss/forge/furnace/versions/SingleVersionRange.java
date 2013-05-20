/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SingleVersionRange implements VersionRange
{
   private Version version;

   public SingleVersionRange(Version version)
   {
      Assert.notNull(version, "Version must not be null.");
      Assert.notNull(version.getVersionString(), "Version must not be null.");
      if (version.getVersionString().isEmpty())
         throw new IllegalArgumentException("Version must not be empty.");

      this.version = version;
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   @Override
   public boolean isExact()
   {
      return true;
   }

   @Override
   public Version getMin()
   {
      return version;
   }

   @Override
   public Version getMax()
   {
      return version;
   }

   @Override
   public boolean includes(Version version)
   {
      return version != null && this.version.getVersionString().equals(version);
   }

   @Override
   public VersionRange getIntersection(VersionRange... ranges)
   {
      for (VersionRange range : ranges)
      {
         if (range.includes(version))
            return this;
      }
      return new EmptyVersionRange();
   }

}
