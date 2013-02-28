/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.util;

import java.util.Arrays;

import org.jboss.forge.container.util.Assert;
import org.jboss.forge.ui.metadata.UICategory;

/**
 * Utility for creating hierarchical {@link UICategory} instances.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Categories
{
   private static final String DEFAULT = "";

   /**
    * Using the given category names, produce a hierarchical {@link UICategory} instance.
    */
   public static UICategory create(String... categories)
   {
      if (categories == null || categories.length == 0)
      {
         return new UICategoryImpl(DEFAULT, null);
      }
      else if (categories.length == 1)
      {
         return new UICategoryImpl(categories[0], null);
      }
      else
      {
         return new UICategoryImpl(categories[0], Categories.create(Arrays.copyOfRange(categories, 1,
                  categories.length)));
      }
   }

   private static class UICategoryImpl implements UICategory
   {
      private String name;
      private UICategory subCategory;

      public UICategoryImpl(String name, UICategory subCategory)
      {
         Assert.notNull(name, "Name must not be null.");

         this.name = name;
         this.subCategory = subCategory;
      }

      @Override
      public String getName()
      {
         return name;
      }

      @Override
      public UICategory getSubCategory()
      {
         return subCategory;
      }

      @Override
      public String toString()
      {
         return getName() + "/" + (getSubCategory() != null ? getSubCategory() : "");
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((subCategory == null) ? 0 : subCategory.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         UICategoryImpl other = (UICategoryImpl) obj;
         if (name == null)
         {
            if (other.name != null)
               return false;
         }
         else if (!name.equals(other.name))
            return false;
         if (subCategory == null)
         {
            if (other.subCategory != null)
               return false;
         }
         else if (!subCategory.equals(other.subCategory))
            return false;
         return true;
      }
   }
}
