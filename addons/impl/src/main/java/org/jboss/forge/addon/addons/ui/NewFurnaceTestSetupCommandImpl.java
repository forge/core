/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.ui;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

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
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.repositories.AddonRepository;

/**
 * Creates a Furnace Test case
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NewFurnaceTestSetupCommandImpl extends AbstractProjectCommand implements NewFurnaceTestSetupCommand
{
   private UISelectMany<AddonId> addonDependencies;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      addonDependencies = factory.createSelectMany("addonDependencies", AddonId.class).setLabel("Dependency addons")
               .setDescription("Addons this test depends upon");

      Set<AddonId> choices = new TreeSet<>();
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      for (AddonRepository repository : furnace.getRepositories())
      {
         for (AddonId id : repository.listEnabled())
         {
            if (id.getName().contains(":"))
               choices.add(id);
         }
      }
      addonDependencies.setValueChoices(choices);
      addonDependencies.setDefaultValue(new ArrayList<AddonId>());
      builder.add(addonDependencies);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Addon: Test Setup")
               .description("Add addon test setup to this project")
               .category(Categories.create("Forge", "Generate"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      FacetFactory facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      DependencyInstaller dependencyInstaller = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
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
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

}
