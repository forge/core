/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.git.facet.GitIgnoreFacet;
import org.jboss.forge.addon.git.gitignore.GitIgnoreConfig;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

@FacetConstraint({ GitFacet.class })
public class GitIgnoreSetupCommandImpl extends AbstractGitCommand implements GitIgnoreSetupCommand
{
   @SuppressWarnings("rawtypes")
   private UIInput<FileResource> templateRepoDir;
   private UIInput<String> repository;

   private GitIgnoreConfig gitIgnoreConfig;
   FacetFactory facetFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata
               .from(super.getMetadata(context), this.getClass())
               .name("GitIgnore: Setup")
               .description(
                        "Create .gitignore files based on template files from https://github.com/github/gitignore.git.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      templateRepoDir = getInputComponentFactory().createInput("templateRepoDir", FileResource.class)
               .setLabel("Checkout directory")
               .setDescription("Where should the gitignore template repository be installed at?")
               .setDefaultValue(getDefaultCheckoutDir());
      repository = getInputComponentFactory().createInput("repository", String.class)
               .setLabel("Repository")
               .setDescription("Do you want to provide a different repository location for gitignore templates?")
               .setDefaultValue(getGitIgnoreConfig().defaultRemoteRepository());

      builder.add(templateRepoDir).add(repository);
   }

   private FileResource<?> getDefaultCheckoutDir()
   {
      return (FileResource<?>) getResourceFactory().create(getGitIgnoreConfig().defaultLocalRepository());
   }

   @Override
   public void validate(UIValidationContext context)
   {
      FileResource<?> checkoutDir = templateRepoDir.getValue();
      if (checkoutDir.exists())
      {
         if (!checkoutDir.isDirectory())
         {
            context.addValidationError(templateRepoDir,
                     "File " + checkoutDir + " is not a directory.");
         }
         if (!checkoutDir.listResources().isEmpty())
         {
            context.addValidationError(templateRepoDir,
                     "Directory " + checkoutDir + " is not empty");
         }
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      FileResource<?> checkoutDir = templateRepoDir.getValue();
      if (!checkoutDir.exists())
      {
         checkoutDir.mkdirs();
      }

      getGitIgnoreConfig().setLocalRepository(checkoutDir.getFullyQualifiedName());
      getGitIgnoreConfig().setRemoteRepository(repository.getValue());

      getFacetFactory().install(getSelectedProject(context), GitIgnoreFacet.class);
      return Results.success("GITIGNORE has been installed.");
   }

   private GitIgnoreConfig getGitIgnoreConfig()
   {
      if (gitIgnoreConfig == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         gitIgnoreConfig = addonRegistry.getServices(GitIgnoreConfig.class).get();
      }
      return gitIgnoreConfig;
   }

}
