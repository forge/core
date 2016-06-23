/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import static org.jboss.forge.addon.git.constants.GitConstants.GIT_DIRECTORY;

import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class GitSetupCommandImpl extends AbstractGitCommand implements GitSetupCommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("Git: Setup")
               .description("Prepares the project for functioning in GIT context");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      getFacetFactory().install(getSelectedProject(context), GitFacet.class);
      return Results.success("GIT has been installed.");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context)
               && !getSelectedProject(context).getRoot().reify(DirectoryResource.class).getChildDirectory(GIT_DIRECTORY)
                        .exists();
   }
}
