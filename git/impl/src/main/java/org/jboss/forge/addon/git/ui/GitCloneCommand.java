/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.ui;

import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
public class GitCloneCommand extends AbstractGitCommand
{

   @Inject
   @WithAttributes(label = "URI", description = "Git repository URI", required = true)
   private UIInput<String> uri;

   @Inject
   @WithAttributes(label = "Target directory", required = true)
   private UIInput<DirectoryResource> targetDirectory;

   @Inject
   private GitUtils gitUtils;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), this.getClass()).name("GIT: Clone")
               .description("Clone a GIT repository");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
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
      Git clone = gitUtils.clone(cloneFolder, uri.getValue());
      gitUtils.close(clone);
      context.getUIContext().setSelection(cloneFolder);
      return Results.success();
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      DirectoryResource folder = targetDirectory.getValue();
      if (folder.exists() && (!folder.isDirectory() || !folder.listResources().isEmpty()))
      {
         validator.addValidationError(targetDirectory, "The specified target directory should not exist or should be empty directory");
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}
