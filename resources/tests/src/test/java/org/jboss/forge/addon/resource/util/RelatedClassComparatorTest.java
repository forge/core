/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.util;

import java.util.Arrays;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.junit.Assert;
import org.junit.Test;

public class RelatedClassComparatorTest
{
   @Test
   public void testClassOrder()
   {
      Class<?>[] actuals = { DirectoryResource.class, FileResource.class, Resource.class };
      Class<?>[] expecteds = { Resource.class, FileResource.class,
               DirectoryResource.class };
      Arrays.sort(actuals, new RelatedClassComparator());
      Assert.assertArrayEquals(expecteds, actuals);
   }
}
