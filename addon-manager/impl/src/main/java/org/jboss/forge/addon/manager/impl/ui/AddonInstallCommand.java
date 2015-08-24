package org.jboss.forge.addon.manager.impl.ui;

import java.util.Set;
import java.util.TreeSet;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.manager.impl.utils.CoordinateUtils;
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
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.furnace.versions.Version;

public class AddonInstallCommand extends AbstractUICommand implements AddonCommandConstants
{
   private static final String FORGE_ADDON_GROUP_ID = "org.jboss.forge.addon:";

   private UIInput<String> coordinate;

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
      InputComponentFactory factory = builder.getInputComponentFactory();
      coordinate = factory.createInput("coordinate", String.class).setLabel("Coordinate")
               .setDescription("The addon's \"groupId:artifactId,version\" coordinate")
               .setNote("The addon's \"groupId:artifactId,version\" coordinate").setRequired(true);
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
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
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                  String value)
         {
            Set<String> items = new TreeSet<String>();
            if (Strings.isNullOrEmpty(value))
            {
               items.add(FORGE_ADDON_GROUP_ID);
            }
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
               Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
               AddonDependencyResolver resolver = SimpleContainer
                        .getServices(getClass().getClassLoader(), AddonDependencyResolver.class).get();
               CoordinateUtils.resolveCoordinate(coordinate, furnace.getVersion(), resolver);
            }
            catch (Exception e)
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
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      AddonManager addonManager = SimpleContainer.getServices(getClass().getClassLoader(), AddonManager.class).get();
      AddonDependencyResolver resolver = SimpleContainer
               .getServices(getClass().getClassLoader(), AddonDependencyResolver.class).get();
      Version version = furnace.getVersion();
      AddonId addonId = CoordinateUtils.resolveCoordinate(coordinate.getValue(), version, resolver);
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

}
