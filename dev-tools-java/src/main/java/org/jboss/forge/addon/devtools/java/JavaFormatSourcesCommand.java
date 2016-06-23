/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.devtools.java;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.xml.resources.XMLResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
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
import org.jboss.forge.roaster.model.util.FormatterProfileReader;
import org.jboss.forge.roaster.model.util.Strings;

public class JavaFormatSourcesCommand extends AbstractUICommand
{

   private UIInput<XMLResource> profilepath;

   @SuppressWarnings("rawtypes")
   private UIInputMany<FileResource> sources;

   private UIInput<String> profilename;

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
      InputComponentFactory inputFactory = builder.getInputComponentFactory();
      sources = inputFactory.createInputMany("sources", 's', FileResource.class);
      sources.setDescription("The folder or file where the java sources will be formatted");

      profilepath = inputFactory.createInput("profilepath", 'p', XMLResource.class);
      profilepath.setDescription("The eclipse code format profile");

      profilename = inputFactory.createInput("profilename", 'n', String.class);
      profilename.setDescription("The eclipse code format profile name");

      Furnace furnace = SimpleContainer.getFurnace(this.getClass().getClassLoader());
      Configuration userConfig = furnace.getAddonRegistry().getServices(Configuration.class).get();
      ResourceFactory resourceFactory = furnace.getAddonRegistry().getServices(ResourceFactory.class).get();
      String profileName = userConfig.getString(JavaResource.FORMATTER_PROFILE_NAME_KEY);
      if (!Strings.isNullOrEmpty(profileName))
      {
         profilename.setDefaultValue(profileName);
      }

      String path = userConfig.getString(JavaResource.FORMATTER_PROFILE_PATH_KEY);
      if (!Strings.isNullOrEmpty(path))
      {
         XMLResource resource = resourceFactory.create(XMLResource.class, new File(path));
         profilepath.setDefaultValue(resource);
      }

      builder.add(sources).add(profilepath).add(profilename);
   }

   @SuppressWarnings({ "rawtypes" })
   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      XMLResource formatProfileLocation = profilepath.getValue();
      Iterable<FileResource> formatSources = sources.getValue();
      String formatterName = profilename.getValue();
      List<FileResource<?>> fileResourceList = new ArrayList<>();

      if (!formatSources.iterator().hasNext())
      {
         UISelection<Resource> us = context.getUIContext().getInitialSelection();
         Resource<?> rs = us.get();

         FileResource<?> fr = rs.reify(FileResource.class);
         fileResourceList.add(fr);
      }

      else
         for (FileResource<?> fileResource : formatSources)
            fileResourceList.add(fileResource);

      Properties formatProfile = null;

      if (formatProfileLocation == null || !formatProfileLocation.exists())
         formatProfile = null;

      else
      {
         try (InputStream is = formatProfileLocation.getResourceInputStream())
         {
            FormatterProfileReader reader = FormatterProfileReader.fromEclipseXml(is);
            formatProfile = reader.getPropertiesFor(formatterName);
         }
      }
      if (formatProfile != null)
      {
         format(fileResourceList, formatProfile);
         return Results.success("Files Formatted Sucessfully");
      }
      else
      {
         return Results.fail("No format profile found to be applied");
      }
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
            JavaResource file = fileResource.reify(JavaResource.class);
            file.setContents(fileResource.getResourceInputStream(), formatProfile);
         }
      }

   }
}