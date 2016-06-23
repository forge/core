/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
public class GitCloneCommandImpl extends AbstractGitCommand implements GitCloneCommand
{

   private UIInput<String> uri;
   private UIInput<DirectoryResource> targetDirectory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("Git: Clone")
               .description("Clone a GIT repository");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      this.uri = getInputComponentFactory().createInput("uri", String.class).setLabel("URI")
               .setDescription("Git repository URI").setRequired(true);
      this.targetDirectory = getInputComponentFactory().createInput("targetDirectory", DirectoryResource.class)
               .setLabel("Target directory").setRequired(true);
      builder.add(uri).add(targetDirectory);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      DirectoryResource cloneFolder = targetDirectory.getValue();
      if (!cloneFolder.exists())
      {
         cloneFolder.mkdirs();
      }
      Git clone = null;
      try
      {
         CloneCommand cloneCommand = Git.cloneRepository().setURI(uri.getValue())
                  .setDirectory(cloneFolder.getUnderlyingResourceObject());
         cloneCommand.setProgressMonitor(new ProgressMonitorAdapter(context.getProgressMonitor()));
         clone = cloneCommand.call();
      }
      finally
      {
         getGitUtils().close(clone);
      }
      context.getUIContext().setSelection(cloneFolder);
      return Results.success();
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      DirectoryResource folder = targetDirectory.getValue();
      if (folder == null || (folder.exists() && (!folder.isDirectory() || !folder.listResources().isEmpty())))
      {
         validator.addValidationError(targetDirectory,
                  "The specified target directory should not exist or should be empty directory");
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}
