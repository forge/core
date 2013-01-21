/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class CompositeDependencyFilterTest
{

   @Test
   public void testAccept()
   {
      final AtomicInteger counter = new AtomicInteger();
      DependencyFilter first = new DependencyFilter()
      {

         @Override
         public boolean accept(Dependency dependency)
         {
            return false;
         }
      };
      DependencyFilter second = new DependencyFilter()
      {

         @Override
         public boolean accept(Dependency dependency)
         {
            counter.incrementAndGet();
            return true;
         }
      };
      CompositeDependencyFilter filter = new CompositeDependencyFilter(first, second);
      boolean returnedValue = filter.accept(DependencyBuilder.create("org.jboss.forge:forge-api"));
      assertFalse(returnedValue);
      assertEquals("Second Filter should not have been called", 0, counter.get());
   }
}
