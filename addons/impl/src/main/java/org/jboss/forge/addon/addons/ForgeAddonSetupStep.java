/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Called when the Next button is pressed and the {@link ForgeAddonProjectType} is selected in NewProjectWizard
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeAddonSetupStep implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Create API, Impl, SPI, Tests, and Addon modules")
   private UIInput<Boolean> splitProjects;

   @Inject
   @WithAttributes(label = "Furnace Version:", required = true)
   private UISelectOne<Version> forgeVersion;

   @Inject
   @WithAttributes(label = "Depend on these addons:")
   private UISelectMany<AddonId> addons;

   @Inject
   private DependencyResolver dependencyResolver;

   @Inject
   private Furnace forge;

   @Inject
   private AddonProjectConfigurator addonProjectFactory;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("Furnace Addon Setup")
               .description("Enable Furnace Addon development in your project.")
               .category(Categories.create("Project", "Furnace"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      configureVersions();
      configureSplitProjects();
      configureAddonDependencies();

      builder.add(forgeVersion).add(splitProjects).add(addons);
   }

   private void configureSplitProjects()
   {
      splitProjects.setDefaultValue(Boolean.FALSE);
   }

   private void configureAddonDependencies()
   {
      Set<AddonId> choices = new HashSet<AddonId>();
      for (AddonRepository repository : forge.getRepositories())
      {
         for (AddonId id : repository.listEnabled())
         {
            choices.add(id);
         }
      }
      addons.setValueChoices(choices);
   }

   private void configureVersions()
   {
      Coordinate c = CoordinateBuilder.create().setGroupId("org.jboss.forge.furnace").setArtifactId("furnace");
      List<Version> versions = new ArrayList<Version>();
      for (Coordinate versionCoord : dependencyResolver.resolveVersions(DependencyQueryBuilder.create(c)))
      {
         versions.add(new SingleVersion(versionCoord.getVersion()));
      }
      forgeVersion.setValueChoices(versions);
      forgeVersion.setDefaultValue(forge.getVersion());
   }

   @Override
   public void validate(UIValidationContext validator)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      final Project project = (Project) context.getAttribute(Project.class);
      Iterable<AddonId> dependencyAddons = addons.getValue();
      if (splitProjects.getValue())
      {
         addonProjectFactory.setupAddonProject(project, forgeVersion.getValue(), dependencyAddons);
      }
      else
      {
         addonProjectFactory.setupSimpleAddonProject(project, forgeVersion.getValue(), dependencyAddons);
      }

      return Results.success();
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }
}