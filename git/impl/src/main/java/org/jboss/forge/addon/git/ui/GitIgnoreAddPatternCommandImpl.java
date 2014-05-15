package org.jboss.forge.addon.git.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class GitIgnoreAddPatternCommandImpl extends AbstractGitCommand implements GitIgnoreAddPatternCommand
{

   @Inject
   @WithAttributes(label = "Pattern", required = true)
   private UIInput<String> pattern;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GITIGNORE: Add pattern")
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
