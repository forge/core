/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.example.commands;

import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIRegion;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.util.Selections;

/**
 * Open a {@link FileResource} in the selected {@link UIRegion} for demo purposes
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class OpenSelectedRegionCommand implements UICommand
{
   @Inject
   @WithAttributes(label = "Resource", type = InputType.FILE_PICKER, required = true)
   private UIInput<FileResource<?>> resource;

   @Inject
   @WithAttributes(label = "Start Line Number", required = true)
   private UIInput<Integer> startLineNumber;

   @Inject
   @WithAttributes(label = "End Line Number", required = true)
   private UIInput<Integer> endLineNumber;

   @Inject
   @WithAttributes(label = "Start Position", required = true)
   private UIInput<Integer> startPosition;

   @Inject
   @WithAttributes(label = "End Position", required = true)
   private UIInput<Integer> endPosition;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Open Selected Region")
               .category(Categories.create("UI", "Example"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(resource).add(startLineNumber).add(endLineNumber).add(startPosition).add(endPosition);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      UISelection<FileResource<?>> selection = Selections.from(MockRegion::new, Arrays.asList(resource.getValue()));
      uiContext.setSelection(selection);
      return Results.success();
   }

   public class MockRegion implements UIRegion<FileResource<?>>
   {
      private FileResource<?> resource;

      public MockRegion(FileResource<?> resource)
      {
         this.resource = resource;
      }

      @Override
      public int getStartPosition()
      {
         return startPosition.getValue();
      }

      @Override
      public int getEndPosition()
      {
         return endPosition.getValue();
      }

      @Override
      public int getStartLine()
      {
         return startLineNumber.getValue();
      }

      @Override
      public int getEndLine()
      {
         return endLineNumber.getValue();
      }

      @Override
      public Optional<String> getText()
      {
         return null;
      }

      @Override
      public FileResource<?> getResource()
      {
         return resource;
      }
   }
}