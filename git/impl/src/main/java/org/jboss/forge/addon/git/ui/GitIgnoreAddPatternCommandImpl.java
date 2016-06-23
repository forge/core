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
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class GitIgnoreAddPatternCommandImpl extends AbstractGitCommand implements GitIgnoreAddPatternCommand
{

   private UIInput<String> pattern;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GitIgnore: Add pattern")
               .description("Add pattern to .gitignore");
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
      builder.add(pattern);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      GitIgnoreResource gitIgnore = gitIgnoreResource(context.getUIContext());
      gitIgnore.addPattern(pattern.getValue());

      return Results.success("Pattern added to the .gitignore in the current directory");
   }

}
