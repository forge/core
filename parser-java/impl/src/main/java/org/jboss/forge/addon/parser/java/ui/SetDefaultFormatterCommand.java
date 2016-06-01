/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;

/**
 * Sets the default formatter when saving {@link JavaResource} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SetDefaultFormatterCommand extends AbstractUICommand
{
   @Inject
   @WithAttributes(label = "Formatter Name", description = "The formatter name specified in the Eclipse formatter XML file")
   private UIInput<String> formatterName;

   @Inject
   @WithAttributes(label = "Formatter Path", description = "An Eclipse formatter XML file with the profile to use", required = true)
   private UIInput<FileResource<?>> formatterPath;

   @Inject
   private Configuration configuration;

   @Inject
   private ResourceFactory resourceFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: Set Default Formatter")
               .description("Sets the default formatter for the Java resources")
               .category(Categories.create("Java"));
   }

   @SuppressWarnings("unchecked")
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      String profileName = configuration.getString(JavaResource.FORMATTER_PROFILE_NAME_KEY);
      if (!Strings.isNullOrEmpty(profileName))
      {
         formatterName.setDefaultValue(profileName);
      }
      String path = configuration.getString(JavaResource.FORMATTER_PROFILE_PATH_KEY);
      if (!Strings.isNullOrEmpty(path))
      {
         FileResource<?> resource = resourceFactory.create(FileResource.class, new File(path));
         formatterPath.setDefaultValue(resource);
      }
      builder.add(formatterName).add(formatterPath);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      String name = formatterName.getValue();
      FileResource<?> path = formatterPath.getValue();
      if (!Strings.isNullOrEmpty(name))
      {
         configuration.setProperty(JavaResource.FORMATTER_PROFILE_NAME_KEY, name);
      }
      else
      {
         configuration.clearProperty(JavaResource.FORMATTER_PROFILE_NAME_KEY);
      }
      configuration.setProperty(JavaResource.FORMATTER_PROFILE_PATH_KEY, path.getFullyQualifiedName());
      return Results.success();
   }
}
