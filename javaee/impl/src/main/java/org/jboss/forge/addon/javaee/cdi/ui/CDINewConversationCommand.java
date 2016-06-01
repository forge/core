/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.SyntaxError;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CDINewConversationCommand extends AbstractUICommand
{
   @Inject
   @WithAttributes(label = "Named", defaultValue = "")
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Conversation Field Name", defaultValue = "conversation", required = true)
   private UIInput<String> conversationFieldName;

   @Inject
   @WithAttributes(label = "Timeout")
   private UIInput<Long> timeout;

   @Inject
   @WithAttributes(label = "Begin Method Name", defaultValue = "beginConversation", required = true)
   private UIInput<String> beginMethodName;

   @Inject
   @WithAttributes(label = "End Method Name", defaultValue = "endConversation", required = true)
   private UIInput<String> endMethodName;

   @Inject
   @WithAttributes(label = "Overwrite")
   private UIInput<Boolean> overwrite;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(named).add(timeout).add(conversationFieldName).add(beginMethodName).add(endMethodName).add(overwrite);

   }

   @Override
   public void validate(UIValidationContext validator)
   {
      boolean overwriteValue = overwrite.getValue() != null && overwrite.getValue();
      JavaResource resource = (JavaResource) validator.getUIContext().getInitialSelection().get();
      JavaClassSource javaClass;
      if (!overwriteValue)
      {
         try
         {
            String fieldName = conversationFieldName.getValue();
            String beginName = beginMethodName.getValue();
            String endName = endMethodName.getValue();
            javaClass = resource.getJavaType();
            if (javaClass.hasField(fieldName) && !javaClass.getField(fieldName).getType().isType(Conversation.class))
            {
               validator.addValidationError(conversationFieldName, "Field [" + fieldName + "] already exists.");
            }
            if (javaClass.hasMethodSignature(beginName)
                     && (javaClass.getMethod(beginName).getParameters().size() == 0))
            {
               validator.addValidationError(beginMethodName, "Method [" + beginName + "] exists.");
            }
            if (javaClass.hasMethodSignature(endName) && (javaClass.getMethod(endName).getParameters().size() == 0))
            {
               validator.addValidationError(endMethodName, "Method [" + endName + "] exists.");
            }
         }
         catch (FileNotFoundException e)
         {
            validator.addValidationError(null, "The selected resource file was not found");
         }
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      JavaResource resource = (JavaResource) uiContext.getInitialSelection().get();
      String name = named.getValue();
      String fieldName = conversationFieldName.getValue();
      String beginName = beginMethodName.getValue();
      String endName = endMethodName.getValue();
      Boolean overwriteValue = overwrite.getValue();
      UIOutput output = uiContext.getProvider().getOutput();
      if (resource.exists())
      {
         JavaType<?> javaType = resource.getJavaType();
         if (javaType.isClass())
         {
            JavaClassSource javaClass = (JavaClassSource) javaType;

            if (javaClass.hasField(fieldName) && !javaClass.getField(fieldName).getType().isType(Conversation.class))
            {
               if (overwriteValue)
               {
                  javaClass.removeField(javaClass.getField(fieldName));
               }
               else
               {
                  return Results.fail("Field [" + fieldName + "] already exists.");
               }
            }
            if (javaClass.hasMethodSignature(beginName)
                     && (javaClass.getMethod(beginName).getParameters().size() == 0))
            {
               if (overwriteValue)
               {
                  javaClass.removeMethod(javaClass.getMethod(beginName));
               }
               else
               {
                  return Results.fail("Method [" + beginName + "] exists.");
               }
            }
            if (javaClass.hasMethodSignature(endName) && (javaClass.getMethod(endName).getParameters().size() == 0))
            {
               if (overwriteValue)
               {
                  javaClass.removeMethod(javaClass.getMethod(endName));
               }
               else
               {
                  return Results.fail("Method [" + endName + "] exists.");
               }
            }

            javaClass.addField().setPrivate().setName(fieldName).setType(Conversation.class)
                     .addAnnotation(Inject.class);

            MethodSource<JavaClassSource> beginMethod = javaClass.addMethod().setName(beginName).setReturnTypeVoid()
                     .setPublic();
            if (Strings.isNullOrEmpty(name))
            {
               beginMethod.setBody(fieldName + ".begin();");
            }
            else
            {
               beginMethod.setBody(fieldName + ".begin(\"" + name + "\");");
            }

            if (timeout.getValue() != null)
            {
               beginMethod.setBody(beginMethod.getBody() + "\n" + fieldName + ".setTimeout(" + timeout + ");");
            }

            javaClass.addMethod().setName(endName).setReturnTypeVoid().setPublic()
                     .setBody(fieldName + ".end();");

            if (javaClass.hasSyntaxErrors())
            {
               output.err().println("Modified Java class contains syntax errors:");
               for (SyntaxError error : javaClass.getSyntaxErrors())
               {
                  output.err().print(error.getDescription());
               }
            }

            resource.setContents(javaClass);
         }
         else
         {
            return Results.fail("Must operate on a Java Class file");
         }
      }
      return Results.success("Conversation block created");

   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {

      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Conversation")
               .description("Creates a conversation block in the specified method")
               .category(Categories.create("Java EE", "CDI"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      boolean result;
      UISelection<Object> initialSelection = context.getInitialSelection();
      if (initialSelection.isEmpty())
      {
         result = false;
      }
      else
      {
         Object selection = initialSelection.get();
         if (selection instanceof JavaResource)
         {
            try
            {
               result = ((JavaResource) selection).getJavaType().isClass();
            }
            catch (FileNotFoundException e)
            {
               result = false;
            }
         }
         else
         {
            result = false;
         }
      }
      return result;
   }
}
