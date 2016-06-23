/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.git.facet.GitIgnoreFacet;
import org.jboss.forge.addon.git.gitignore.GitIgnoreTemplateGroup;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint({ GitFacet.class, GitIgnoreFacet.class })
public class GitIgnoreListTemplatesCommandImpl extends AbstractGitCommand implements GitIgnoreListTemplatesCommand
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GitIgnore: List templates")
               .description("List all available .gitignore templates");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Installed .gitignore templates:").append("\n");

      for (GitIgnoreTemplateGroup group : getSelectedProject(context).getFacet(GitIgnoreFacet.class).list())
      {
         sb.append("============= " + group.getName() + " =============").append("\n");
         for (String template : group.getTemplates())
         {
            sb.append(template).append("\n");
         }
      }

      return Results.success(sb.toString());
   }

}
