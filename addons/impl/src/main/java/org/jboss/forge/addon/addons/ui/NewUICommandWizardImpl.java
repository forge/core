/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.ui;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.parser.java.utils.Packages;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NewUICommandWizardImpl extends AbstractJavaSourceCommand implements NewUICommandWizard
{
   @Inject
   @WithAttributes(label = "Command name", required = false)
   private UIInput<String> commandName;

   @Inject
   @WithAttributes(label = "Categories", required = false)
   private UIInputMany<String> categories;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(NewUICommandWizardImpl.class)
               .name("Addon: New UI Command").description("Generates a UICommand implementation")
               .category(Categories.create("Forge", "Generate"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      categories.setDefaultValue(new ArrayList<String>());
      builder.add(commandName).add(categories);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = super.execute(context);
      if (!(result instanceof Failed))
      {
         JavaResource javaResource = context.getUIContext().getSelection();
         JavaClassSource command = javaResource.getJavaType();

         if (Strings.isNullOrEmpty(commandName.getValue()))
         {
            commandName.setValue(calculateCommandName(command.getName()));
         }

         JavaClassSource javaClass = createCommand(command, commandName.getValue(), categories.getValue());
         Project project = getSelectedProject(context);
         final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         javaResource = java.saveJavaSource(javaClass);
         context.getUIContext().setSelection(javaResource);
      }
      return result;
   }

   private JavaClassSource createCommand(JavaClassSource command, String commandName, Iterable<String> categories)
   {
      command.setSuperType(AbstractUICommand.class);
      command.addImport(UIBuilder.class);
      command.addImport(UIContext.class);
      command.addImport(UIExecutionContext.class);
      command.addImport(UICommandMetadata.class);
      command.addImport(Metadata.class);
      command.addImport(Categories.class);
      command.addImport(Result.class);
      command.addImport(Results.class);

      MethodSource<JavaClassSource> getMetadataMethod = command.addMethod()
               .setPublic()
               .setName("getMetadata")
               .setReturnType(UICommandMetadata.class)
               .setParameters("UIContext context");
      getMetadataMethod.addAnnotation(Override.class);

      String getMetadataMethodBody = "return Metadata.forCommand(" + command.getName() + ".class" + ")\n"
               + "\t.name(\"" + commandName + "\")";
      Iterator<String> iterator = categories.iterator();
      if (iterator.hasNext())
      {
         getMetadataMethodBody += "\t.category(Categories.create(";
         while (iterator.hasNext())
         {
            getMetadataMethodBody += "\"" + iterator.next() + "\"";
            if (iterator.hasNext())
               getMetadataMethodBody += ", ";
         }
         getMetadataMethodBody += "))";
      }
      getMetadataMethodBody += ";";
      getMetadataMethod.setBody(getMetadataMethodBody);

      command.addMethod()
               .setPublic()
               .setName("initializeUI")
               .setReturnTypeVoid()
               .setBody("// not implemented")
               .setParameters("UIBuilder builder")
               .addThrows(Exception.class)
               .addAnnotation(Override.class);

      command.addMethod()
               .setPublic()
               .setName("execute")
               .setReturnType(Result.class)
               .setParameters("UIExecutionContext context")
               .setBody("return Results.fail(\"Not implemented!\");")
               .addThrows(Exception.class)
               .addAnnotation(Override.class);

      // build the thing
      return command;
   }

   @Override
   protected Class<? extends JavaSource<?>> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      Project project = getSelectedProject(context);
      return Packages.toValidPackageName(project.getFacet(MetadataFacet.class).getTopLevelPackage()) + ".commands";
   }

   @Override
   protected String getType()
   {
      return "UI Command";
   }

   static String calculateCommandName(String value)
   {
      StringBuilder builder = new StringBuilder();

      if (Strings.isNullOrEmpty(value))
      {
         throw new IllegalArgumentException("It isn't possible to parse a null value");
      }

      if (value.toLowerCase().endsWith("command"))
      {
         value = value.substring(0, value.toLowerCase().lastIndexOf("command"));
      }

      value = value
               .replaceFirst(Character.toString(value.charAt(0)), Character.toString(value.charAt(0)).toLowerCase());
      for (int index = 0; index < value.length(); index++)
      {
         char charValue = value.charAt(index);
         if (index > 0 && Character.isUpperCase(charValue) && Character.isLowerCase(value.charAt(index - 1)))
         {
            builder.append("-").append(Character.toLowerCase(charValue));
         }
         else
         {
            builder.append(Character.toLowerCase(charValue));
         }
      }
      return builder.toString();
   }
}
