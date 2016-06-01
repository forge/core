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
class CommandNavigationResultEntry implements NavigationResultEntry
{
   private final UICommand command;

   public CommandNavigationResultEntry(UICommand command)
   {
      this.command = command;
   }

   @Override
   public UICommand getCommand(AddonRegistry addonRegistry, UIContext context)
   {
      return command;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((command == null) ? 0 : command.hashCode());
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
      CommandNavigationResultEntry other = (CommandNavigationResultEntry) obj;
      if (command == null)
      {
         if (other.command != null)
            return false;
      }
      else if (!command.equals(other.command))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "CommandNavigationResultEntry [command=" + command + "]";
   }

}
