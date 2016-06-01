/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result.navigation;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResultEntry;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class CompositeNavigationResultEntry implements NavigationResultEntry
{
   private final UICommandMetadata metadata;
   private final Iterable<NavigationResultEntry> entries;

   private UICommand command;

   CompositeNavigationResultEntry(UICommandMetadata metadata, Iterable<NavigationResultEntry> entries)
   {
      this.metadata = metadata;
      this.entries = entries;
   }

   @Override
   public UICommand getCommand(AddonRegistry addonRegistry, UIContext context)
   {
      if (command == null)
      {
         List<UICommand> commands = new ArrayList<>();
         for (NavigationResultEntry entry : entries)
         {
            UICommand command = entry.getCommand(addonRegistry, context);
            commands.add(command);
         }
         command = new CompositeCommand(metadata, commands);
      }
      return command;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((entries == null) ? 0 : entries.hashCode());
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
      CompositeNavigationResultEntry other = (CompositeNavigationResultEntry) obj;
      if (entries == null)
      {
         if (other.entries != null)
            return false;
      }
      else if (!entries.equals(other.entries))
         return false;
      return true;
   }

}
