/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Starts a {@link ResourceTransaction}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TransactionStartCommand extends AbstractShellCommand
{
   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private TrackChangesSettings trackChangesSettings;

   @Inject
   @WithAttributes(label = "Timeout", shortName = 't', defaultValue = "0")
   private UIInput<Integer> timeout;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("transaction-start")
               .description("Starts a transaction");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(timeout);
   }

   @Override
   public Result execute(UIExecutionContext shellContext) throws Exception
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();

      if (transaction.isStarted())
      {
         if (trackChangesSettings.isTrackChanges() && !trackChangesSettings.isInForeignTransaction())
            transaction.rollback();
         else
            return Results.fail("Resource Transaction is already started");
      }
      if (timeout.getValue() != null)
      {
         transaction.setTransactionTimeout(timeout.getValue());
      }
      transaction.begin();
      return Results.success("Resource Transaction started");
   }

   @Override
   public boolean isEnabled(ShellContext context)
   {
      return super.isEnabled(context) & !resourceFactory.getTransaction().isStarted();
   }
}
