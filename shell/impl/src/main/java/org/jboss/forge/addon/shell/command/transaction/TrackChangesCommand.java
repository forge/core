/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command.transaction;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Enable/disable an automatic {@link ResourceTransaction} on each command. Also toggles displaying changes to console.
 * 
 */
public class TrackChangesCommand extends AbstractShellCommand
{
   @Inject
   private TrackChangesSettings settings;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("track-changes")
               .description("Initiate a transaction for each executed command.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public Result execute(UIExecutionContext shellContext) throws Exception
   {
      if (settings.isTrackChanges())
      {
         settings.setTrackChanges((Shell) shellContext.getUIContext().getProvider(), false);
         return Results.success("Resource change tracking is OFF.");
      }
      else
      {
         settings.setTrackChanges((Shell) shellContext.getUIContext().getProvider(), true);
         return Results.success("Resource change tracking is ON.");
      }
   }
}
