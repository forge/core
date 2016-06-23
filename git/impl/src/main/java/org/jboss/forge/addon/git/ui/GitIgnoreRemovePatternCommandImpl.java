/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
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

public class GitIgnoreRemovePatternCommandImpl extends AbstractGitCommand implements GitIgnoreRemovePatternCommand
{

   private UIInput<String> pattern;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("Git: Remove pattern")
               .description("Remove pattern from .gitignore");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context) && isGitIgnoreSelected(context);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      this.pattern = getInputComponentFactory().createInput("pattern", String.class).setLabel("Pattern")
               .setRequired(true);
      pattern.setCompleter(new GitIgnorePatternCompleter(gitIgnoreResource(builder.getUIContext())));
      builder.add(pattern);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      GitIgnoreResource gitIgnore = gitIgnoreResource(context.getUIContext());
      gitIgnore.removePattern(pattern.getValue());

      return Results.success("Pattern removed from the .gitignore in the current directory");
   }

   private static class GitIgnorePatternCompleter implements UICompleter<String>
   {
      private final GitIgnoreResource resource;

      public GitIgnorePatternCompleter(GitIgnoreResource resource)
      {
         this.resource = resource;
      }

      @Override
      public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
      {
         return resource.getPatterns();
      }

   }
}
