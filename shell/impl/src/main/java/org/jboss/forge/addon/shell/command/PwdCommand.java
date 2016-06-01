/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PwdCommand extends AbstractShellCommand
{

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // no arguments
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("pwd")
               .description("Print the full filename of the current working directory.");
   }

   @Override
   public Result execute(UIExecutionContext shellContext) throws Exception
   {
      UIContext uiContext = shellContext.getUIContext();
      UISelection<Resource<?>> selection = uiContext.getInitialSelection();
      Resource<?> currentResource = selection.get();
      if (currentResource != null)
      {
         UIOutput output = uiContext.getProvider().getOutput();
         output.out().println(currentResource.getFullyQualifiedName());
      }
      return Results.success();
   }

}
