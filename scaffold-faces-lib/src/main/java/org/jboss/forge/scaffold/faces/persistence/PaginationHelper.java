package org.jboss.forge.scaffold.faces.persistence;

import java.util.List;

public abstract class PaginationHelper<T>
{
   private final int pageSize;
   private int page;

   public PaginationHelper(int pageSize)
   {
      this.pageSize = pageSize;
   }

   public abstract int getItemsCount();

   public abstract List<T> createPageDataModel();

   public int getPageFirstItem()
   {
      return this.page * this.pageSize;
   }

   public int getPageLastItem()
   {
      int i = getPageFirstItem() + this.pageSize - 1;
      int count = getItemsCount() - 1;
      if (i > count) {
         i = count;
      }
      if (i < 0) {
         i = 0;
      }
      return i;
   }

   public boolean isHasNextPage()
   {
      return (this.page + 1) * this.pageSize + 1 <= getItemsCount();
   }

   public boolean isHasPreviousPage()
   {
      return this.page > 0;
   }

   public int getPageSize()
   {
      return this.pageSize;
   }

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }
}
