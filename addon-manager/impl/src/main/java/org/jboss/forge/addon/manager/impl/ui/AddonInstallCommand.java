package org.jboss.forge.addon.manager.impl.ui;

import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

public class AddonInstallCommand extends AbstractUICommand implements AddonCommandConstants
{
   private static final String FORGE_ADDON_GROUP_ID = "org.jboss.forge.addon:";

   @Inject
   private AddonManager addonManager;

   @Inject
   private AddonDependencyResolver resolver;

   @Inject
   @WithAttributes(label = "Coordinate", description = "The addon's \"groupId:artifactId,version\" coordinate", required = true)
   private UIInput<String> coordinate;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private Furnace furnace;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata.from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_INSTALL_COMMAND_NAME : ADDON_INSTALL_COMMAND_NAME_NO_GUI)
               .description(ADDON_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = Projects.getSelectedProject(projectFactory, builder.getUIContext());
      if (project != null)
      {
         MetadataFacet facet = project.getFacet(MetadataFacet.class);
         Coordinate c = facet.getOutputDependency().getCoordinate();
         coordinate.setDefaultValue(AddonId.from(c.getGroupId() + ":" + c.getArtifactId(), c.getVersion())
                  .toCoordinates());
      }

      coordinate.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            Set<String> items = new TreeSet<String>();
            items.add("org.jboss.forge.addon:");
            return items;
         }
      });

      coordinate.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            String coordinate = (String) context.getCurrentInputComponent().getValue();
            try
            {
               resolveCoordinate(coordinate);
            }
            catch (IllegalArgumentException e)
            {
               context.addValidationError(context.getCurrentInputComponent(), "\"" + coordinate
                        + "\" is not a valid Addon coordinate");
            }
         }
      });

      builder.add(coordinate);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      AddonId addonId = resolveCoordinate(coordinate.getValue());
      try
      {
         addonManager.install(addonId).perform();
         return Results.success("Addon " + addonId.toCoordinates() + " was installed successfully.");
      }
      catch (Throwable t)
      {
         return Results.fail("Addon " + addonId.toCoordinates() + " could not be installed.", t);
      }
   }

   // TODO this method needs to be abstracted into a utility
   private AddonId resolveCoordinate(String addonCoordinates) throws IllegalArgumentException
   {
      Version runtimeAPIVersion = furnace.getVersion();
      AddonId addon;
      // This allows forge --install maven
      if (addonCoordinates.contains(","))
      {
         if (addonCoordinates.contains(":"))
         {
            addon = AddonId.fromCoordinates(addonCoordinates);
         }
         else
         {
            addon = AddonId.fromCoordinates(FORGE_ADDON_GROUP_ID + addonCoordinates);
         }
      }
      else
      {
         AddonId[] versions;
         String coordinate;
         if (addonCoordinates.contains(":"))
         {
            coordinate = addonCoordinates;
            versions = resolver.resolveVersions(addonCoordinates).get();
         }
         else
         {
            coordinate = FORGE_ADDON_GROUP_ID + addonCoordinates;
            versions = resolver.resolveVersions(coordinate).get();
         }

         if (versions.length == 0)
         {
            throw new IllegalArgumentException("No Artifact version found for " + coordinate);
         }
         else
         {
            AddonId selected = null;
            for (int i = versions.length - 1; selected == null && i >= 0; i--)
            {
               String apiVersion = resolver.resolveAPIVersion(versions[i]).get();
               if (apiVersion != null
                        && Versions.isApiCompatible(runtimeAPIVersion, new SingleVersion(apiVersion)))
               {
                  selected = versions[i];
               }
            }
            if (selected == null)
            {
               throw new IllegalArgumentException("No compatible addon API version found for " + coordinate
                        + " for API " + runtimeAPIVersion);
            }

            addon = selected;
         }
      }
      return addon;
   }
}
