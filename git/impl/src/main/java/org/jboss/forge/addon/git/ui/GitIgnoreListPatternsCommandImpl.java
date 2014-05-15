package org.jboss.forge.addon.git.ui;

import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class GitIgnoreListPatternsCommandImpl extends AbstractGitCommand implements GitIgnoreListPatternsCommand
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GITIGNORE: List patterns")
               .description("List available .gitignore patterns");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context) && isGitIgnoreSelected(context);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      StringBuilder sb = new StringBuilder();
      GitIgnoreResource gitIgnore = gitIgnoreResource(context.getUIContext());
      for (String pattern : gitIgnore.getPatterns())
      {
         sb.append(pattern).append("\n");
      }

      return Results.success(sb.toString());
   }

}
