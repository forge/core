/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.ui.catalog;

import java.io.PrintStream;
import java.util.List;

import org.jboss.forge.addon.manager.catalog.AddonDescriptor;
import org.jboss.forge.addon.manager.catalog.AddonDescriptorCatalogRegistry;
import org.jboss.forge.addon.manager.impl.ui.AddonCommandConstants;
import org.jboss.forge.addon.manager.impl.ui.AddonInstallCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonSearchCommand implements UICommand, AddonCommandConstants {
   private UIInputMany<String> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      this.arguments = factory.createInputMany("arguments", String.class)
               .setLabel("Arguments")
               .setRequired(true);
      builder.add(arguments);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata
               .forCommand(AddonInstallCommand.class)
               .name(gui ? ADDON_SEARCH_COMMAND_NAME : ADDON_SEARCH_COMMAND_NAME_NO_GUI)
               .description(ADDON_SEARCH_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Iterable<String> value = arguments.getValue();
      List<AddonDescriptor> addons = getAddonDescriptorCatalogRegistry().find(value.iterator().next());
      UIOutput output = context.getUIContext().getProvider().getOutput();
      PrintStream out = output.out();
      SimpleTable table = SimpleTable.of()
               .nextRow()
               .nextCell().addLine("Id")
               .nextCell().addLine("Name")
               .nextCell().addLine("Description");
      for (AddonDescriptor addon : addons)
      {
         table.nextRow()
                  .nextCell().addLine(addon.getId())
                  .nextCell().addLine(addon.getName())
                  .nextCell().addLine(addon.getDescription());
      }
      Util.print(Border.SINGLE_LINE.apply(table.toGrid()), out);
      return Results.success();
   }

   AddonDescriptorCatalogRegistry getAddonDescriptorCatalogRegistry() {
      final AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      return addonRegistry.getServices(AddonDescriptorCatalogRegistry.class).get();

   }
}
