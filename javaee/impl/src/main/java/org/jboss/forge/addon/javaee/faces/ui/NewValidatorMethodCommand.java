/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import java.io.FileNotFoundException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.faces.FacesOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewValidatorMethodCommand extends AbstractJavaEECommand
{

   @Inject
   @WithAttributes(label = "Target Java class", description = "The Java class in which the method will be created", required = true, type = InputType.JAVA_CLASS_PICKER)
   private UIInput<JavaResource> target;

   @Inject
   @WithAttributes(label = "Validator method name", required = true)
   private UIInput<String> named;

   @Inject
   private FacesOperations operations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Faces: New Validator Method")
               .description("Create a new JSF validator method")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JSF"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Object selection = builder.getUIContext().getInitialSelection().get();
      if (selection instanceof JavaResource)
      {
         target.setDefaultValue((JavaResource) selection);
      }
      builder.add(target).add(named);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      try
      {
         JavaClass source = (JavaClass) target.getValue().reify(JavaResource.class).getJavaSource();
         Method<JavaClass> method = source.getMethod(named.getValue(), FacesContext.class, UIComponent.class,
                  Object.class);

         if (method != null)
            validator.addValidationError(named, "A validator with that name already exists in '"
                     + target.getValue().getJavaSource().getQualifiedName() + "'");

         super.validate(validator);
      }
      catch (FileNotFoundException e)
      {
         validator.addValidationError(target, "Target Java class not found.");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Method<JavaClass> method = operations.addValidatorMethod(target.getValue().reify(JavaResource.class),
               named.getValue());
      return Results.success("Validator method '" + method.toSignature() + "' created");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}
