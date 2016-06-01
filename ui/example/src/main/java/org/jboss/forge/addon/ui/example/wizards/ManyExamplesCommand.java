/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ManyExamplesCommand extends AbstractUICommand
{

   @Inject
   private UIInputMany<String> manyString;

   @Inject
   private UIInputMany<FileResource<?>> manyFiles;

   @Inject
   private UIInputMany<DirectoryResource> manyDirectories;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(manyString).add(manyFiles).add(manyDirectories);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Many String: " + manyString.getValue()).append("\n");
      sb.append("Many Files: " + manyFiles.getValue()).append("\n");
      sb.append("Many Directories: " + manyDirectories.getValue()).append("\n");
      return Results.success(sb.toString());
   }
}
