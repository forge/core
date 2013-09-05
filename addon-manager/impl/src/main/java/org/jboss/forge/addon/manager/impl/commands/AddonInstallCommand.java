package org.jboss.forge.addon.manager.impl.commands;

import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.AddonManager;

public class AddonInstallCommand extends AbstractUICommand implements AddonCommandConstants
{

   @Inject
   private AddonManager addonManager;

   @Inject
   @WithAttributes(label = "Group ID", description = "The value of <groupId> from the addon's pom.xml", required = true)
   private UIInput<String> groupId;

   @Inject
   @WithAttributes(label = "Name", description = "The value of <artifactId> from the addon's pom.xml", required = true)
   private UIInput<String> name;

   @Inject
   @WithAttributes(label = "Version", description = "The value of <version> from the addon's pom.xml", required = true)
   private UIInput<String> version;

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public Metadata getMetadata()
   {
      return Metadata.from(super.getMetadata(), getClass()).name(ADDON_INSTALL_COMMAND_NAME)
               .description(ADDON_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      final String topLevelPackage;
      if (project != null)
      {
         MetadataFacet facet = project.getFacet(MetadataFacet.class);
         topLevelPackage = facet.getTopLevelPackage();
         groupId.setDefaultValue(topLevelPackage);
         name.setDefaultValue(facet.getProjectName());
         version.setDefaultValue(facet.getProjectVersion());
      }
      else
      {
         topLevelPackage = null;
      }
      groupId.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            Set<String> items = new TreeSet<String>();
            if (topLevelPackage != null)
               items.add(topLevelPackage);
            items.add("org.jboss.forge.addon");
            return items;
         }
      });
      builder.add(groupId).add(name).add(version);
   }

   @Override
   public Result execute(UIContext context)
   {
      String coordinates = getCoordinates();
      try
      {
         addonManager.install(AddonId.fromCoordinates(coordinates)).perform();
         return Results.success("Addon " + coordinates + " was installed succesfully.");
      }
      catch (Throwable t)
      {
         return Results.fail("Addon " + coordinates + " could not be installed.", t);
      }
   }

   protected String getCoordinates()
   {
      return groupId.getValue() + ':' + name.getValue() + ',' + version.getValue();
   }

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      Project project = null;
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         project = projectFactory.findProject(initialSelection.get());
      }
      return project;
   }
}
