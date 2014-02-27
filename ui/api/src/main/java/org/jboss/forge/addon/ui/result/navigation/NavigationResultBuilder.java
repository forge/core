/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result.navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.NavigationResultEntry;

/**
 * Builds a {@link NavigationResult} object
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public final class NavigationResultBuilder
{
   private List<NavigationResultEntry> entries = new LinkedList<>();

   private NavigationResultBuilder()
   {
   }

   public static NavigationResultBuilder create()
   {
      return new NavigationResultBuilder();
   }

   public static NavigationResultBuilder create(NavigationResult result)
   {
      NavigationResultBuilder builder = new NavigationResultBuilder();
      if (result != null)
      {
         builder.entries.addAll(Arrays.asList(result.getNext()));
      }
      return builder;
   }

   public NavigationResultBuilder add(Class<? extends UICommand> type)
   {
      entries.add(new ClassNavigationResultEntry(type));
      return this;
   }

   public NavigationResultBuilder add(UICommand command)
   {
      entries.add(new CommandNavigationResultEntry(command));
      return this;
   }

   public NavigationResultBuilder add(UICommandMetadata metadata, Iterable<Class<? extends UICommand>> types)
   {
      List<NavigationResultEntry> internalEntries = new ArrayList<>();
      for (Class<? extends UICommand> type : types)
      {
         internalEntries.add(new ClassNavigationResultEntry(type));
      }
      entries.add(new CompositeNavigationResultEntry(metadata, internalEntries));
      return this;
   }

   public NavigationResult build()
   {
      if (entries.isEmpty())
      {
         return null;
      }
      else
      {
         return new NavigationResultImpl(entries);
      }
   }
}
