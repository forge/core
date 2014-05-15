package org.jboss.forge.addon.git.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.git.facet.GitIgnoreFacet;
import org.jboss.forge.addon.git.gitignore.GitIgnoreConfig;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint({ GitFacet.class })
public class GitIgnoreSetupCommandImpl extends AbstractGitCommand implements GitIgnoreSetupCommand
{

   @Inject
   @WithAttributes(label = "Checkout directory", description = "Where should the gitignore" +
            " template repository be installed at?")
   private UIInput<FileResource<?>> templateRepoDir;

   @Inject
   @WithAttributes(label = "Repository", description = "Do you want to provide a different repository" +
            " location for gitignore templates?")
   private UIInput<String> repository;

   @Inject
   private GitIgnoreConfig config;

   @Inject
   private ResourceFactory factory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private GitIgnoreFacet facet;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata
               .from(super.getMetadata(context), this.getClass())
               .name("GITIGNORE: Setup")
               .description(
                        "Create .gitignore files based on template files from https://github.com/github/gitignore.git.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      templateRepoDir.setDefaultValue(getDefaultCheckoutDir());
      repository.setDefaultValue(config.defaultRemoteRepository());

      builder.add(templateRepoDir).add(repository);
   }

   private FileResource<?> getDefaultCheckoutDir()
   {
      return (FileResource<?>) factory.create(config.defaultLocalRepository());
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

      config.setLocalRepository(checkoutDir.getFullyQualifiedName());
      config.setRemoteRepository(repository.getValue());

      if (facetFactory.install(getSelectedProject(context), facet))
      {
         return Results.success("GITIGNORE has been installed.");
      }
      return Results.fail("Could not install GITIGNORE.");
   }

}
