/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addons.facets.ForgeAddonAPIFacet;
import org.jboss.forge.addons.facets.ForgeAddonFacet;
import org.jboss.forge.addons.facets.ForgeAddonImplFacet;
import org.jboss.forge.addons.facets.ForgeAddonTestFacet;
import org.jboss.forge.addons.facets.ForgeSimpleAddonFacet;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
import org.jboss.forge.projects.facets.PackagingFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UISelectMany;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Categories;
import org.jboss.forge.ui.util.Metadata;
import org.jboss.forge.ui.wizard.UIWizardStep;

/**
 * Called when the Next button is pressed and the {@link ForgeAddonProjectType} is selected in NewProjectWizard
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings("unchecked")
public class ForgeAddonSetupStep implements UIWizardStep
{
   @Inject
   private UIInput<Boolean> splitProjects;

   @Inject
   private UISelectMany<AddonId> addons;

   @Inject
   private DependencyInstaller dependencyInstaller;

   @Inject
   private Forge forge;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("Forge Addon Setup")
               .description("Enable Forge Addon development in your project.")
               .category(Categories.create("Project", "Forge"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      splitProjects.setLabel("Create API,Implementation,Tests and Addon modules").setDefaultValue(
               Boolean.FALSE);
      addons.setLabel("Depend on these addons:");
      Set<AddonId> choices = new HashSet<AddonId>();
      for (AddonRepository repository : forge.getRepositories())
      {
         for (AddonId id : repository.listEnabled())
         {
            choices.add(id);
         }
      }
      addons.setValueChoices(choices);
      builder.add(splitProjects).add(addons);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Project project = (Project) context.getAttribute(Project.class);
      if (splitProjects.getValue())
      {
         // Project is the parent project
         DirectoryResource projectRoot = project.getProjectRoot();
         project.getFacet(PackagingFacet.class).setPackagingType("pom");
         project.getProjectRoot().getChild("src").delete(true);
         installSelectedAddons(project, true);
         // FIXME: Support multimodule projects

         // Create ADDON Project
         createAddonProject(projectRoot);
         // Create API Project
         createAPIProject(projectRoot);
         // Create IMPL Project
         createImplProject(projectRoot);
         // Create TESTS Project
         createTestsProject(projectRoot);
      }
      else
      {
         configureAddonProject(project);
         installSelectedAddons(project, false);
      }
      return Results.success();
   }

   private Project createAddonProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("addon");
      Project project = projectFactory.createProject(location);
      configureAddonProject(project);
      return project;
   }

   private Project createAPIProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("api");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonAPIFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

   private Project createImplProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("impl");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonImplFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

   private Project createTestsProject(DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("tests");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonTestFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

   /**
    * Configure addon
    *
    * @param project
    * @return
    */
   private void configureAddonProject(Project project)
   {
      project.install(facetFactory.create(ForgeAddonFacet.class, project));
   }

   private void installSelectedAddons(Project project, boolean managed)
   {
      for (AddonId addon : addons.getValue())
      {
         String[] mavenCoords = addon.getName().split(":");
         DependencyBuilder dependency = DependencyBuilder.create().setGroupId(mavenCoords[0])
                  .setArtifactId(mavenCoords[1])
                  .setVersion(addon.getVersion().getVersionString()).setClassifier("forge-addon");
         if (managed)
         {
            dependencyInstaller.installManaged(project, dependency);
         }
         else
         {
            dependencyInstaller.install(project, dependency);
         }
      }
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }

}