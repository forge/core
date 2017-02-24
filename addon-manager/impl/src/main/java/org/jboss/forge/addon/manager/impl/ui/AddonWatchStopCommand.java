/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.ui;

import org.jboss.forge.addon.manager.watch.AddonWatchService;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonWatchStopCommand implements UICommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Addon: Watch Stop")
               .description("Stop watching addons marked as SNAPSHOT")
               .category(Categories.create("Addon"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return getAddonWatchService().isStarted();
   }

   @Override
   public Result execute(final UIExecutionContext executionContext) throws Exception
   {
      getAddonWatchService().stop();
      return Results.success();
   }

   /**
    * @return the addonWatchService
    */
   private AddonWatchService getAddonWatchService()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), AddonWatchService.class).get();
   }
}
