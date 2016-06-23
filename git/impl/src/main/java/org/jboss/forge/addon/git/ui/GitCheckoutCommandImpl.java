/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint(GitFacet.class)
public class GitCheckoutCommandImpl extends AbstractGitCommand implements GitCheckoutCommand
{
   private UIInput<String> branchName;
   private UIInput<Boolean> create;
   private UISelectOne<SetupUpstreamMode> trackingMode;
   private UIInput<Boolean> force;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("Git: Checkout")
               .description("Checkout a branch from GIT repository or create a new one");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      this.branchName = getInputComponentFactory().createInput("branchName", String.class).setLabel("Branch name")
               .setDefaultValue("master");
      this.create = getInputComponentFactory().createInput("create", Boolean.class).setLabel("Create branch")
               .setDefaultValue(false);
      this.trackingMode = getInputComponentFactory().createSelectOne("trackingMode", SetupUpstreamMode.class)
               .setLabel("Track").setDescription("Remote tracking mode")
               .setDefaultValue(SetupUpstreamMode.SET_UPSTREAM);
      this.force = getInputComponentFactory().createInput("forge", Boolean.class).setLabel("Force")
               .setDefaultValue(false);
      builder.add(branchName).add(create).add(trackingMode).add(force);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      Result result = null;
      if (project != null)
      {
         try (Git git = getGitUtils().git(project.getRoot().reify(DirectoryResource.class)))
         {
            getGitUtils().checkout(git, branchName.getValue(), create.getValue(),
                     trackingMode.getValue(), force.getValue());
         }
         result = Results.success();
      }
      else
      {
         result = Results.fail("This command should be executed in the context of a project");
      }

      return result;
   }

}
