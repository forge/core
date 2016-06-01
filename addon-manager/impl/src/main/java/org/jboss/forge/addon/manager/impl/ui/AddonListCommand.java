/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.repositories.AddonRepository;

public class AddonListCommand extends AbstractUICommand implements AddonCommandConstants
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name(ADDON_LIST_COMMAND_NAME)
               .description(ADDON_LIST_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      Set<AddonId> choices = new TreeSet<>();
      for (AddonRepository repository : furnace.getRepositories())
      {
         choices.addAll(repository.listEnabled());
      }
      UIOutput output = context.getUIContext().getProvider().getOutput();
      PrintStream out = output.out();
      out.println("Currently installed addons:");
      for (AddonId addonId : choices)
      {
         out.println(addonId.toCoordinates());
      }
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return !context.getProvider().isGUI();
   }
}
