package org.jboss.forge.addon.git.ui;

import javax.inject.Inject;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint(GitFacet.class)
public class GitCheckoutCommandImpl extends AbstractGitCommand implements GitCheckoutCommand
{

   @Inject
   @WithAttributes(label = "Branch name", defaultValue = "master")
   private UIInput<String> branchName;

   @Inject
   @WithAttributes(label = "Create branch", defaultValue = "false")
   private UIInput<Boolean> create;

   @Inject
   @WithAttributes(label = "Track", description = "Remote tracking mode", defaultValue = "SET_UPSTREAM")
   private UISelectOne<SetupUpstreamMode> trackingMode;

   @Inject
   @WithAttributes(label = "Force", defaultValue = "false")
   private UIInput<Boolean> force;

   @Inject
   private GitUtils gitUtils;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GIT: Checkout")
               .description("Checkout a branch from GIT repository or create a new one");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(branchName).add(create).add(trackingMode).add(force);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      Result result = null;
      if (project != null)
      {
         Git git = gitUtils.git(project.getRootDirectory());
         gitUtils.checkout(git, branchName.getValue(), create.getValue(),
                  trackingMode.getValue(), force.getValue());
         gitUtils.close(git);
         result = Results.success();
      }
      else
      {
         result = Results.fail("This command should be executed in the context of a project");
      }

      return result;
   }

}
