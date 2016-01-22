/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.example.commands;

import java.io.PrintStream;
import java.util.Optional;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIRegion;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Displays the selected {@link UIRegion} for the selected resources
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ShowSelectedRegionCommand implements UICommand
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Show Selected Region")
               .category(Categories.create("UI", "Example"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return context.getInitialSelection().size() > 0;
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      UIOutput output = uiContext.getProvider().getOutput();
      UISelection<Object> selection = uiContext.getSelection();
      if (selection.isEmpty())
      {
         return Results.fail("No resource selected");
      }
      else
      {
         for (Object resource : selection)
         {
            if (resource != null)
            {
               PrintStream out = output.out();
               output.info(out, "Selected Resource: " + resource);
               Optional<UIRegion<Object>> optionalRegion = selection.getRegion();
               if (optionalRegion.isPresent())
               {
                  UIRegion<Object> region = optionalRegion.get();
                  String msg = String.format("Start Line: %s, End Line: %s, Start: %s, End: %s, Text: %s",
                           region.getStartLine(),
                           region.getEndLine(), region.getStartPosition(), region.getEndPosition(),
                           region.getText().orElseGet(() -> "Empty"));
                  return Results.success(msg);
               }
               else
               {
                  return Results.fail("No selected region found");
               }
            }
         }
         return Results.success();
      }
   }

}
