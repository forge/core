/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.devtools.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.xml.resources.XMLResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.roaster.Roaster;

public class JavaFormatSourcesCommand extends AbstractUICommand
{

   private final InputComponentFactory inputFactory;

   private UIInput<XMLResource> profile;

   @SuppressWarnings("rawtypes")
   private UIInputMany<FileResource> sources;

   public JavaFormatSourcesCommand()
   {
      Furnace furnace = SimpleContainer.getFurnace(this.getClass().getClassLoader());
      this.inputFactory = furnace.getAddonRegistry().getServices(InputComponentFactory.class).get();
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata
               .forCommand(getClass())
               .name("Java: Format Sources")
               .description(
                        "command to format a file (or folder recursively) of Java source files")
               .category(Categories.create("Java"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      sources = inputFactory.createInputMany("sources", 's', FileResource.class);
      sources.setDescription("The folder or file where the java sources will be formatted");

      profile = inputFactory.createInput("profile", 'p', XMLResource.class);
      profile.setDescription("The eclipse code format profile");

      builder.add(sources).add(profile);
   }

   @SuppressWarnings("rawtypes")
   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      XMLResource formatProfileLocation = profile.getValue();
      Iterable<FileResource> formatSources = sources.getValue();
      List<FileResource<?>> fileResourceList = new ArrayList<>();

      if (!formatSources.iterator().hasNext())
      {
         UISelection<Resource> us = context.getUIContext().getInitialSelection();
         Resource<?> rs = us.get();

         FileResource<?> fr = (FileResource) rs.reify(FileResource.class);
         fileResourceList.add(fr);
      }

      else
         for (FileResource<?> fileResource : formatSources)
            fileResourceList.add(fileResource);

      Properties formatProfile;

      if (formatProfileLocation == null || !formatProfileLocation.exists())
         formatProfile = null;

      else
      {
         formatProfile = new Properties();
         formatProfile.load(formatProfileLocation.getResourceInputStream());
      }

      format(fileResourceList, formatProfile);

      return Results.success("Files Formatted Sucessfully");
   }

   // Formatting the file or folder(recursively).
   private static void format(List<FileResource<?>> fileResourceList, Properties formatProfile)
   {
      for (FileResource<?> fileResource : fileResourceList)
      {
         if (fileResource.isDirectory())
         {
            List<FileResource<?>> newFileResourceList = new ArrayList<>();
            List<Resource<?>> resourceList = fileResource.listResources();

            for (Resource<?> resource : resourceList)
               newFileResourceList.add(resource.reify(FileResource.class));

            format(newFileResourceList, formatProfile);
         }

         else if (fileResource instanceof JavaResource)
         {
            if (formatProfile == null)
               fileResource.setContents(Roaster.format(fileResource.getContents()));
            else
               fileResource.setContents(Roaster.format(formatProfile, fileResource.getContents()));
         }
      }

   }
}
