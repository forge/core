/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import org.jboss.forge.addon.ui.metadata.UICategory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CategoriesTest
{
   @Test
   public void testNullArgs()
   {
      UICategory category = Categories.create();
      Assert.assertNotNull(category);
      Assert.assertEquals(Categories.createDefault().getName(), category.getName());
      Assert.assertNull(category.getSubCategory());
   }

   @Test
   public void testSingleCategory()
   {
      UICategory category = Categories.create("ONE");
      Assert.assertNotNull(category);
      Assert.assertEquals("ONE", category.getName());
      Assert.assertNull(category.getSubCategory());
   }

   @Test
   public void testMultipleCategory()
   {
      UICategory category = Categories.create("ONE", "TWO", "THREE");
      Assert.assertNotNull(category);
      Assert.assertEquals("ONE", category.getName());
      Assert.assertNotNull(category.getSubCategory());
      Assert.assertEquals("TWO", category.getSubCategory().getName());
      Assert.assertNotNull(category.getSubCategory().getSubCategory());
      Assert.assertEquals("THREE", category.getSubCategory().getSubCategory().getName());
      Assert.assertNull(category.getSubCategory().getSubCategory().getSubCategory());
   }

}
