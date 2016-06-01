/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui.update;

import java.io.File;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Contains commands for updating the Forge distribution
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUpdateAbortCommand extends AbstractUICommand
{
   @Override
   public boolean isEnabled(UIContext context)
   {
      UIProvider provider = context.getProvider();
      if (!provider.isEmbedded() && !provider.isGUI())
      {
         File forgeHomeDir = OperatingSystemUtils.getForgeHomeDir();
         if (forgeHomeDir == null)
            return false;
         File updateDir = new File(forgeHomeDir, ".update");
         return updateDir.exists();
      }
      return false;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Forge: Update Abort").description("Aborts a previous forge update")
               .category(Categories.create("Forge", "Manage"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // no inputs
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ResourceFactory resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
               .get();
      DirectoryResource forgeHome = resourceFactory.create(DirectoryResource.class,
               OperatingSystemUtils.getForgeHomeDir());
      DirectoryResource updateDirectory = forgeHome.getChildDirectory(".update");
      if (updateDirectory.exists())
      {
         if (updateDirectory.delete(true))
         {
            return Results
                     .success("Update files were deleted. Run 'forge-update' if you want to update this installation again.");
         }
         else
         {
            return Results.fail("Could not abort. Try to run 'forge-update-abort' again");
         }
      }
      else
      {
         return Results.success("No update files found");
      }
   }
}
