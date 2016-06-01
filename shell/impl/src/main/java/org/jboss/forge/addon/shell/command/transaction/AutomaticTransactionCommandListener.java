/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

@Vetoed
public class AutomaticTransactionCommandListener implements CommandExecutionListener
{
   private final ResourceFactory factory;
   private final OtherTransactionStartedListener otherTransactionListener;
   private final AggregateChangesTransactionListener aggregator;

   public AutomaticTransactionCommandListener(ResourceFactory factory,
            OtherTransactionStartedListener otherTransactionListener,
            AggregateChangesTransactionListener aggregator)
   {
      this.factory = factory;
      this.otherTransactionListener = otherTransactionListener;
      this.aggregator = aggregator;
   }

   @Override
   public void preCommandExecuted(UICommand command, UIExecutionContext context)
   {
      ResourceTransaction transaction = factory.getTransaction();
      if (!transaction.isStarted())
      {
         otherTransactionListener.disable();
         transaction.begin();
         otherTransactionListener.enable();
      }
   }

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      ResourceTransaction transaction = factory.getTransaction();
      if (!otherTransactionListener.isInForeignTransaction())
      {
         if (result instanceof Failed)
         {
            if (transaction.isStarted())
            {
               transaction.rollback();
            }
         }
         else
         {
            if (transaction.isStarted())
            {
               transaction.commit();
            }

            List<ResourceEvent> events = new ArrayList<>(aggregator.getResourceEvents());
            aggregator.clear();
            Collections.sort(events, new Comparator<ResourceEvent>()
            {
               @Override
               public int compare(ResourceEvent left, ResourceEvent right)
               {
                  return left.getResource().getFullyQualifiedName()
                           .compareTo(right.getResource().getFullyQualifiedName());
               }
            });
            Map<EventType, Set<String>> organizedEvents = organize(events);
            UIOutput output = context.getUIContext().getProvider().getOutput();

            for (Entry<EventType, Set<String>> entry : organizedEvents.entrySet())
            {
               switch (entry.getKey())
               {
               case CREATED:
                  for (String resourceName : entry.getValue())
                  {
                     output.out().println("Created  " + resourceName);
                  }
                  break;
               case DELETED:
                  for (String resourceName : entry.getValue())
                  {
                     output.out().println("Deleted  " + resourceName);
                  }
                  break;
               case MODIFIED:
                  for (String resourceName : entry.getValue())
                  {
                     output.out().println("Modified " + resourceName);
                  }

                  break;
               }
            }
         }
      }
   }

   @Override
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
   {
      ResourceTransaction transaction = factory.getTransaction();
      if (transaction.isStarted())
      {
         transaction.rollback();
      }
   }

   private enum EventType
   {
      CREATED, DELETED, MODIFIED
   }

   private Map<EventType, Set<String>> organize(final Iterable<ResourceEvent> events)
   {
      Map<EventType, Set<String>> result = new HashMap<>();
      for (ResourceEvent e : events)
      {
         Resource<?> resource = e.getResource();
         final EventType eventType;
         if (e instanceof ResourceCreated)
         {
            eventType = EventType.CREATED;
         }
         else if (e instanceof ResourceDeleted)
         {
            eventType = EventType.DELETED;
         }
         else
         {
            eventType = EventType.MODIFIED;
         }
         Set<String> list = result.get(eventType);
         if (list == null)
         {
            list = new TreeSet<>();
            result.put(eventType, list);
         }
         list.add(resource.getFullyQualifiedName());
      }

      // Remove Resource from MODIFIED if Created or Deleted
      Set<String> createdResources = result.get(EventType.CREATED);
      Set<String> modifiedResource = result.get(EventType.MODIFIED);
      Set<String> deletedResources = result.get(EventType.DELETED);
      if (modifiedResource != null)
      {
         Iterator<String> it = modifiedResource.iterator();
         while (it.hasNext())
         {
            String resource = it.next();
            if ((createdResources != null && createdResources.contains(resource)) ||
                     (deletedResources != null && deletedResources.contains(resource)))
            {
               it.remove();
            }
         }
      }
      return result;
   }
}