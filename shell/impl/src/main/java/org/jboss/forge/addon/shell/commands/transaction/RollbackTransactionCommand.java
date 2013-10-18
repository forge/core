/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.commands.transaction;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RollbackTransactionCommand extends AbstractShellCommand
{
   @Inject
   ResourceFactory resourceFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("transaction-rollback")
               .description("Rollbacks a transaction");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public boolean isEnabled(ShellContext context)
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      return transaction.isStarted();
   }

   @Override
   public Result execute(ShellContext shellContext) throws Exception
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      if (!transaction.isStarted())
      {
         return Results.fail("Resource Transaction is not started");
      }
      transaction.rollback();
      return Results.success();
   }

}
