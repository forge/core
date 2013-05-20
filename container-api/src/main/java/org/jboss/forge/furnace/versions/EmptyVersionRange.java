/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

/**
 * A {@link VersionRange} that matches no possible {@link Version} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EmptyVersionRange implements VersionRange
{

   @Override
   public boolean isEmpty()
   {
      return true;
   }

   @Override
   public boolean isExact()
   {
      return false;
   }

   @Override
   public Version getMin()
   {
      return new SingleVersion("");
   }

   @Override
   public Version getMax()
   {
      return new SingleVersion("");
   }

   @Override
   public boolean includes(Version version)
   {
      return false;
   }

   @Override
   public VersionRange getIntersection(VersionRange... ranges)
   {
      return this;
   }

}
