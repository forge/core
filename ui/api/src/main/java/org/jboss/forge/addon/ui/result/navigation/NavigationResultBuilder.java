/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.ui.wizard.UIWizard;

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

   /**
    * Create a new instance of a NavigationResultBuilder
    * 
    * @return A NavigationResultBuilder instance
    */
   public static NavigationResultBuilder create()
   {
      return new NavigationResultBuilder();
   }

   /**
    * Create a new instance of a NavigationResultBuilder using the provided NavigationResult instance as a base.
    * 
    * @param result A NavigationResult whose entries are used as the basis to eventually construct a new
    *           NavigationResult through this NavigationResultBuilder.
    * @return A NavigationResultBuilder instance
    */
   public static NavigationResultBuilder create(NavigationResult result)
   {
      NavigationResultBuilder builder = new NavigationResultBuilder();
      if (result != null && result.getNext() != null)
      {
         builder.entries.addAll(Arrays.asList(result.getNext()));
      }
      return builder;
   }

   /**
    * Add a UICommand type to create a single navigation entry.
    * 
    * @param type The UICommand type to add
    * @return This NavigationResultBuilder instance
    */
   public NavigationResultBuilder add(Class<? extends UICommand> type)
   {
      entries.add(new ClassNavigationResultEntry(type));
      return this;
   }

   /**
    * Add a UICommand instance to create a single navigation entry.
    * 
    * @param command The UICommand to add
    * @return This NavigationResultBuilder instance
    */
   public NavigationResultBuilder add(UICommand command)
   {
      entries.add(new CommandNavigationResultEntry(command));
      return this;
   }

   /**
    * Add a UICommand instance to create a single navigation entry.
    * 
    * @param result The NavigationResult to add
    * @return This NavigationResultBuilder instance
    */
   public NavigationResultBuilder add(NavigationResult result)
   {
      if (result != null && result.getNext() != null)
      {
         entries.addAll(Arrays.asList(result.getNext()));
      }
      return this;
   }

   /**
    * Add multiple UICommand types to create a single navigation entry. Every invocation of this method creates a
    * separate navigation entry. UIWizard types must not be provided as arguments since wizards and wizard steps cannot
    * be combined with other UICommand types in the same navigation entry.
    * 
    * Use the other add(...) methods to add UIWizard types.
    * 
    * @param metadata The command metadata
    * @param types The UICommand types to add as a single navigation entry
    * @return This NavigationResultBuilder instance
    */
   public NavigationResultBuilder add(UICommandMetadata metadata, Iterable<Class<? extends UICommand>> types)
   {
      List<NavigationResultEntry> internalEntries = new ArrayList<>();
      for (Class<? extends UICommand> type : types)
      {
         if (UIWizard.class.isAssignableFrom(type))
         {
            throw new IllegalArgumentException("A UICommand of type " + type + " was added. "
                     + UIWizard.class.getSimpleName() + " instances should be added individually.");
         }
         else
         {
            internalEntries.add(new ClassNavigationResultEntry(type));
         }
      }
      entries.add(new CompositeNavigationResultEntry(metadata, internalEntries));
      return this;
   }

   /**
    * Create a NavigationResult instance representing the entries that have been added.
    * 
    * @return A NavigationResult instance
    */
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
