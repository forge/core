package org.jboss.forge.addon.git.ui;

import static org.jboss.forge.addon.git.constants.GitConstants.GIT_DIRECTORY;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class GitSetupCommandImpl extends AbstractGitCommand implements GitSetupCommand
{

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private GitFacet facet;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GIT: Setup")
               .description("Prepares the project for functioning in GIT context");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), facet))
      {
         return Results.success("GIT has been installed.");
      }
      return Results.fail("Could not install GIT.");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context)
               && !getSelectedProject(context).getRootDirectory().getChildDirectory(GIT_DIRECTORY).exists();
   }
}
