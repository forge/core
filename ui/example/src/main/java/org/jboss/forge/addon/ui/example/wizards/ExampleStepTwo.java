/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ExampleStepTwo implements UIWizardStep
{

   @Inject
   private UIInput<DirectoryResource> location;
   
   @Inject
   @WithAttributes(label = "File or Folder Location", type = InputType.FILE_OR_DIRECTORY_PICKER)
   private UIInput<FileResource<?>> fileOrDirectory;
   
   @Inject
   @WithAttributes(label = "File or Folders Location", type = InputType.FILE_OR_DIRECTORY_PICKER)
   private UIInputMany<FileResource<?>> fileOrDirectories;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Step 2").description("Select a folder");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      location.setLabel("Location:");
      builder.add(location).add(fileOrDirectory).add(fileOrDirectories);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      StringBuilder out = new StringBuilder();
      out.append("location: ").append(location.getValue());
      out.append(System.lineSeparator());
      out.append("fileOrDirectory: ").append(fileOrDirectory.getValue());
      out.append(System.lineSeparator());
      out.append("fileOrDirectories: ").append(fileOrDirectories.getValue());
      out.append(System.lineSeparator());
      return Results.success(out.toString());
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      UISelection<?> selection = context.getInitialSelection();
      return !selection.isEmpty();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }
}
