/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.ui.catalog;

import java.util.StringJoiner;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.jboss.forge.addon.manager.catalog.AddonDescriptor;
import org.jboss.forge.addon.manager.catalog.AddonDescriptorCatalogRegistry;
import org.jboss.forge.addon.manager.impl.ui.AddonCommandConstants;
import org.jboss.forge.addon.manager.impl.ui.AddonInstallCommand;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.script.ScriptContextBuilder;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonInstallFromCatalogCommand implements UICommand, AddonCommandConstants {

   private UISelectOne<AddonDescriptor> addon;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      boolean gui = builder.getUIContext().getProvider().isGUI();
      InputComponentFactory factory = builder.getInputComponentFactory();
      this.addon = factory.createSelectOne("addon", AddonDescriptor.class)
               .setLabel("Addon")
               .setRequired(true)
               .setNote(() -> addon.hasValue() ? addon.getValue().getDescription() : null)
               .setValueChoices(getAddonDescriptorCatalogRegistry().find(""))
               .setItemLabelConverter(gui ? AddonDescriptor::getName : AddonDescriptor::getId);
      builder.add(addon);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata
               .forCommand(AddonInstallCommand.class)
               .name(gui ? ADDON_INSTALL_FROM_CATALOG_COMMAND_NAME : ADDON_INSTALL_FROM_CATALOG_COMMAND_NAME_NO_GUI)
               .description(ADDON_INSTALL_FROM_CATALOG_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ForgeScriptEngineFactory factory = new ForgeScriptEngineFactory();
      ScriptEngine engine = factory.getScriptEngine();
      ScriptContext scriptContext = ScriptContextBuilder.create()
               .currentResource((Resource<?>) context.getUIContext().getSelection().get()).build();
      StringJoiner joiner = new StringJoiner(System.lineSeparator());
      AddonDescriptor descriptor = addon.getValue();
      for (String installCmd : descriptor.getInstallCmd())
      {
         joiner.add(installCmd);
      }
      Object result = engine.eval(joiner.toString(), scriptContext);
      if (result == null)
      {
         return Results.success();
      }
      else
      {
         return Results.success(result.toString());
      }
   }

   AddonDescriptorCatalogRegistry getAddonDescriptorCatalogRegistry() {
      final AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      return addonRegistry.getServices(AddonDescriptorCatalogRegistry.class).get();

   }
}
