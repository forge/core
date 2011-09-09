/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.metawidget.forge.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@RequestScoped
public class Navigation
{
   @Inject
   private Instance<MenuItem> items;

   public List<MenuItem> getListItems()
   {
      List<MenuItem> result = new ArrayList<MenuItem>();
      for (MenuItem item : items) {
         if (item.getItemType() != null)
            result.add(item);
      }
      return result;
   }

   public void setListItems(final List<String> items)
   {
      throw new IllegalStateException("Never call this method.");
   }

   public String toLiteralPath(final MenuItem item)
   {
      String result = "";
      Class<?> type = item.getItemType();
      if (item.getLiteralPath() != null)
      {
         result = item.getLiteralPath();
      }
      else if (type != null)
      {
         result = "/scaffold/" + type.getSimpleName().toLowerCase() + "/list";
      }
      return result;
   }

   public String toLabel(final MenuItem item)
   {
      String result = "Unlabeled Link";
      Class<?> type = item.getItemType();
      if (item.getLabel() != null)
      {
         result = item.getLabel();
      }
      else if (type != null)
      {
         result = type.getSimpleName();
      }
      return result;
   }
}
