/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NewAnnotatedUICommandWizardImpl extends AbstractProjectCommand implements NewAnnotatedUICommandWizard
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(label = "Command name", required = true)
   private UIInput<String> commandName;

   @Inject
   @WithAttributes(label = "Categories", required = false)
   private UIInputMany<String> categories;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(NewAnnotatedUICommandWizardImpl.class)
               .name("Addon: New Annotated UI Command").description("Generates an annotated UICommand implementation")
               .category(Categories.create("Forge", "Generate"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      categories.setDefaultValue(new ArrayList<String>());
      builder.add(commandName).add(categories);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = (JavaResource) context.getUIContext().getInitialSelection().get();
      JavaClassSource commandClass = javaResource.getJavaType();
      commandClass = createCommand(commandClass, commandName.getValue(), categories.getValue());
      javaResource.setContents(commandClass);
      return Results.success("Annotated UICommand created");
   }

   private JavaClassSource createCommand(JavaClassSource command, String commandName, Iterable<String> categories)
   {
      MethodSource<JavaClassSource> commandMethod = command.addMethod().setName(toMethodName(command, commandName))
               .setReturnType(String.class)
               .setBody(String.format("return \"Command %s executed\";", commandName)).setPublic();
      AnnotationSource<JavaClassSource> commandAnn = commandMethod.addAnnotation(Command.class);
      List<String> categoryList = new ArrayList<>(Lists.toList(categories));
      commandAnn.setStringValue("value", commandName);
      if (!categoryList.isEmpty())
      {
         commandAnn.setStringArrayValue("categories", categoryList.toArray(new String[categoryList.size()]));
      }
      // build the thing
      return command;
   }

   private String toMethodName(JavaClassSource targetClass, String name)
   {
      StringBuilder sb = new StringBuilder();
      boolean upperCase = false;
      for (char c : name.toCharArray())
      {
         if (Character.isJavaIdentifierPart(c))
         {
            sb.append(upperCase ? Character.toUpperCase(c) : Character.toLowerCase(c));
            upperCase = false;
         }
         else
         {
            upperCase = true;
         }
      }
      return sb.toString();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      boolean enabled = super.isEnabled(context);
      if (enabled)
      {
         Object initialSelection = context.getInitialSelection().get();
         if (initialSelection instanceof JavaResource)
         {
            try
            {
               enabled = ((JavaResource) initialSelection).getJavaType().isClass();
            }
            catch (FileNotFoundException e)
            {
               enabled = false;
            }
         }
         else
         {
            enabled = false;
         }
      }
      return enabled;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }
}
