/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons;

import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.addons.facets.FurnaceVersionFacet;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

/**
 * Creates a Furnace Test case
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NewFurnaceTestSetupCommand extends AbstractProjectCommand
{

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private Furnace furnace;

   @Inject
   @WithAttributes(label = "Dependency addons", description = "Addons this test depends upon")
   private UISelectMany<AddonId> addonDependencies;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Set<AddonId> choices = new TreeSet<>();
      for (AddonRepository repository : furnace.getRepositories())
      {
         // Avoid immutable repositories
         if (repository instanceof MutableAddonRepository)
         {
            for (AddonId id : repository.listEnabled())
            {
               choices.add(id);
            }
         }
      }
      addonDependencies.setValueChoices(choices);
      builder.add(addonDependencies);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Addon: Test Setup")
               .description("Add addon test setup to this project")
               .category(Categories.create("Addon", "Generate"));
   }

   @Inject
   private DependencyInstaller dependencyInstaller;

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      
      facetFactory.install(project, FurnaceVersionFacet.class);
      project.getFacet(FurnaceVersionFacet.class).setVersion(furnace.getVersion().toString());

      facetFactory.install(project, AddonTestFacet.class);
      for (AddonId addonId : addonDependencies.getValue())
      {
         DependencyBuilder dependency = DependencyBuilder.create(addonId.getName())
                  .setVersion(addonId.getVersion().toString()).setScopeType("test");
         if (!dependencyInstaller.isInstalled(project, dependency))
         {
            dependencyInstaller.install(project, dependency);
         }
      }
      return Results
               .success("Project " + project.getFacet(MetadataFacet.class).getProjectName()
                        + " is now configured for testing");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
