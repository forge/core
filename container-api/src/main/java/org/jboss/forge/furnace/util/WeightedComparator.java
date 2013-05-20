/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

import java.util.Comparator;

/**
 * Compares {@link Weighted} objects.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class WeightedComparator implements Comparator<Weighted>
{
   public static final Comparator<Weighted> INSTANCE = new WeightedComparator();

   private WeightedComparator()
   {
   }

   @Override
   public int compare(final Weighted left, final Weighted right)
   {
      if ((left == null) || (right == null))
      {
         return 0;
      }
      return ((Integer) left.priority()).compareTo(right.priority());
   }

}