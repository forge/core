/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result.navigation;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.NavigationResultEntry;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class ClassNavigationResultEntry implements NavigationResultEntry
{
   private final Class<? extends UICommand> type;

   public ClassNavigationResultEntry(Class<? extends UICommand> type)
   {
      this.type = type;
   }

   @Override
   public UICommand getCommand(AddonRegistry addonRegistry, UIContext context)
   {
      return addonRegistry.getServices(type).get();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      ClassNavigationResultEntry other = (ClassNavigationResultEntry) obj;
      if (type == null)
      {
         if (other.type != null)
            return false;
      }
      else if (!type.getName().equals(other.type.getName()))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "ClassNavigationResultEntry [type=" + type + "]";
   }
}
