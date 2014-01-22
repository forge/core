/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

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

            ArrayList<ResourceEvent> events = new ArrayList<>(aggregator.getResourceEvents());
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

            for (ResourceEvent event : events)
            {
               UIOutput output = context.getUIContext().getProvider().getOutput();
               if (event instanceof ResourceCreated)
               {
                  output.out().println("Created " + event.getResource().getFullyQualifiedName());
               }
               else if (event instanceof ResourceModified)
               {
                  output.out().println("Changed " + event.getResource().getFullyQualifiedName());
               }
               else if (event instanceof ResourceDeleted)
               {
                  output.out().println("Deleted " + event.getResource().getFullyQualifiedName());
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
}