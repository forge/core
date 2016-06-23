/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.git.facet.GitIgnoreFacet;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint({ GitFacet.class, GitIgnoreFacet.class })
public class GitIgnoreUpdateRepoCommandImpl extends AbstractGitCommand implements GitIgnoreUpdateRepoCommand
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GitIgnore: Update templates")
               .description("Update the local .gitignore template repository");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = null;
      try
      {
         getSelectedProject(context).getFacet(GitIgnoreFacet.class).update();
         result = Results.success("Local gitignore repository updated.");
      }
      catch (IOException e)
      {
         result = Results.fail("Error reading local repository: " + e.getMessage(), e);
      }
      catch (GitAPIException e)
      {
         result = Results.fail("Error pulling remote repository: " + e.getMessage(), e);
      }
      return result;
   }

}
