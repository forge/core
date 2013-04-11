/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ForgeAddonSetupStep implements UIWizardStep
{
   @Inject
   private UIInput<Boolean> splitApiImpl;

   @Inject
   private UISelectMany<AddonId> addons;

   @Inject
   private DependencyInstaller dependencyInstaller;

   @Inject
   private Forge forge;

   @Inject
   private FacetFactory facetFactory;

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
      splitApiImpl.setLabel("Split API and Implementation projects?").setDefaultValue(Boolean.FALSE);
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
      builder.add(addons);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Project project = (Project) context.getAttribute(Project.class);
      ForgeAddonFacet facet = facetFactory.create(ForgeAddonFacet.class, project);

      Result result;
      if (project.install(facet))
      {
         for (AddonId addon : addons.getValue())
         {
            String[] mavenCoords = addon.getName().split(":");
            DependencyBuilder dependency = DependencyBuilder.create().setGroupId(mavenCoords[0])
                     .setArtifactId(mavenCoords[1])
                     .setVersion(addon.getVersion().getVersionString()).setClassifier("forge-addon");
            dependencyInstaller.installManaged(project, dependency);
         }
         result = Results.success("Forge project created");
      }
      else
      {
         result = Results.fail("Failure while installing the Forge facet");
      }
      return result;
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }

}
