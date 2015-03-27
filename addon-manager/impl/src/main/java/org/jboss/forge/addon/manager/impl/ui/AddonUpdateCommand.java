package org.jboss.forge.addon.manager.impl.ui;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.manager.impl.utils.CoordinateUtils;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.request.InstallRequest;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;
import org.jboss.forge.furnace.manager.spi.Response;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.versions.Versions;

public class AddonUpdateCommand extends AbstractUICommand implements AddonCommandConstants
{
   @Inject
   private AddonManager addonManager;

   @Inject
   private AddonDependencyResolver resolver;

   @Inject
   @WithAttributes(label = "Name", description = "The addon's \"groupId:artifactId\" name", required = true)
   private UIInput<String> named;

   @Inject
   private Furnace furnace;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata.from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_UPDATE_COMMAND_NAME : ADDON_UPDATE_COMMAND_NAME_NO_GUI)
               .description(ADDON_UPDATE_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      named.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            Set<String> items = new TreeSet<String>();
            Set<Addon> addons = furnace.getAddonRegistry().getAddons();
            for (Addon addon : addons)
            {
               items.add(addon.getId().getName());
            }
            return items;
         }
      });

      named.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            String coordinate = (String) context.getCurrentInputComponent().getValue();
            try
            {
               AddonId addonId = CoordinateUtils.resolveCoordinate(coordinate,
                        Versions.getSpecificationVersionFor(this.getClass()), resolver);
               Set<Addon> addons = furnace.getAddonRegistry().getAddons();
               boolean match = false;
               for (Addon addon : addons)
               {
                  if (addon.getId().getName().equals(addonId.getName()))
                  {
                     match = true;
                     break;
                  }
               }
               if (!match)
               {
                  context.addValidationError(context.getCurrentInputComponent(), "\"" + coordinate
                           + "\" does not refer to any installed Addon coordinate");
               }
            }
            catch (IllegalArgumentException e)
            {
               context.addValidationError(context.getCurrentInputComponent(), "\"" + coordinate
                        + "\" is not a valid Addon coordinate");
            }
         }
      });

      builder.add(named);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      AddonId addonId = CoordinateUtils.resolveCoordinate(named.getValue(),
               Versions.getSpecificationVersionFor(this.getClass()), resolver);
      AddonId maxAddonId = addonId;
      try
      {
         Response<AddonId[]> resolveVersions = resolver.resolveVersions(addonId.getName());

         for (AddonId id : resolveVersions.get())
         {
            if (id.getVersion().compareTo(maxAddonId.getVersion()) > 0)
            {
               maxAddonId = id;
            }
         }
         for (AddonRepository r : furnace.getRepositories())
         {
            List<AddonId> listEnabled = r.listEnabled();
            for (AddonId id : listEnabled)
            {
               if (id.equals(maxAddonId))
               {
                  return Results.fail("Addon " + maxAddonId.toCoordinates()
                           + " is already installed with the most updated version.");
               }
            }
         }
         InstallRequest installRequest = addonManager.install(maxAddonId);
         UIPrompt prompt = context.getPrompt();
         if (prompt.promptBoolean(installRequest.toString() + "\n Do you want to proceed?"))
         {
            installRequest.perform();
            return Results.success("Addon " + maxAddonId.toCoordinates() + " was installed successfully.");
         }
         else
         {
            return Results.fail("Addon update aborted.");
         }

         
      }
      catch (Throwable t)
      {
         return Results.fail("Addon " + maxAddonId.toCoordinates() + " could not be installed.", t);
      }
   }

}
