/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.facet.GitFacet;
import org.jboss.forge.addon.git.facet.GitIgnoreFacet;
import org.jboss.forge.addon.git.gitignore.GitIgnoreTemplateGroup;
import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;

@FacetConstraint({ GitFacet.class, GitIgnoreFacet.class })
public class GitIgnoreCreateCommandImpl extends AbstractGitCommand implements GitIgnoreCreateCommand
{

   private UIInput<String> templates;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      this.templates = getInputComponentFactory().createInput("templates", String.class).setLabel("From templates")
               .setRequired(true);
      templates.setCompleter(new GitIgnoreTemplateCompleter(getSelectedProject(builder.getUIContext())));
      builder.add(templates);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GitIgnore: Create")
               .description("Create .gitignore from templates");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      GitIgnoreFacet facet = getSelectedProject(context).getFacet(GitIgnoreFacet.class);
      GitIgnoreResource resource = gitIgnoreResource(context.getUIContext());
      StringBuffer buffer = new StringBuffer();
      for (String template : templates.getValue().split(" "))
      {
         String content = facet.contentOf(template);
         buffer.append(content).append("\n");
      }
      String content = buffer.toString();
      resource.setContents(content);

      StringBuilder resultString = new StringBuilder();
      resultString.append("Wrote to .gitignore. Content:").append("\n");
      resultString.append(content).append("\n");

      context.getUIContext().setSelection(resource);
      return Results.success(resultString.toString());
   }

   private static class GitIgnoreTemplateCompleter implements UICompleter<String>
   {

      private final Project currentProject;

      private GitIgnoreTemplateCompleter(Project currentProject)
      {
         this.currentProject = currentProject;
      }

      @Override
      public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
      {
         List<String> result = new LinkedList<>();

         String[] values = value.split(" ");

         for (GitIgnoreTemplateGroup group : currentProject.getFacet(GitIgnoreFacet.class).list())
         {
            for (String template : group.getTemplates())
            {
               if (Strings.isNullOrEmpty(values[values.length - 1]) || template.startsWith(values[values.length - 1]))
               {
                  result.add(template);
               }
            }
         }
         return result;
      }

   }
}
