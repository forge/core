/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.javaee.validation.ConstraintOperations;
import org.jboss.forge.addon.parser.java.beans.JavaClassIntrospector;
import org.jboss.forge.addon.parser.java.beans.Property;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.parser.java.JavaClass;

public class SelectFieldWizardStep extends AbstractJavaEECommand implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Property", description = "The property on which the constraint applies", required = true, type = InputType.DROPDOWN)
   private UISelectOne<Property> property;

   @Inject
   @WithAttributes(label = "Constraint", description = "The type of constraint to add", required = true, type = InputType.DROPDOWN)
   private UISelectOne<CoreConstraints> constraint;

   @Inject
   @WithAttributes(label = "Add constraint on the property accessor?")
   private UIInput<Boolean> onAccessor;

   @Inject
   private ConstraintOperations constraintOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupProperty(builder.getUIContext());
      setupConstraint();
      setupAccessor();
      builder.add(property).add(constraint).add(onAccessor);
   }

   private void setupProperty(UIContext context) throws Exception
   {
      JavaResource selectedResource = (JavaResource) context.getAttribute(JavaResource.class);
      JavaClass javaClass = (JavaClass) selectedResource.getJavaSource();
      JavaClassIntrospector introspector = new JavaClassIntrospector(javaClass);
      property.setItemLabelConverter(new Converter<Property, String>()
      {
         @Override
         public String convert(Property source)
         {
            return (source == null) ? null : source.getName();
         }
      });
      List<Property> properties = introspector.getProperties();
      property.setValueChoices(properties);
   }

   private void setupConstraint()
   {
      constraint.setItemLabelConverter(new Converter<CoreConstraints, String>()
      {
         @Override
         public String convert(CoreConstraints source)
         {
            return (source == null) ? null : source.getDescription();
         }
      });
      constraint.setValueChoices(EnumSet.allOf(CoreConstraints.class));
   }

   private void setupAccessor()
   {
      onAccessor.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return property.getValue().isReadable();
         }
      });
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ConstraintType constraintType = constraint.getValue();
      if (constraintType == CoreConstraints.VALID)
      {
         Project project = getSelectedProject(context);
         Result result = constraintOperations.addValidConstraint(project, property.getValue(), onAccessor.getValue());
         return result;
      }
      else
      {
         return Results.success();
      }
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      ConstraintType constraintType = constraint.getValue();
      UIContext uiContext = context.getUIContext();
      uiContext.setAttribute(Property.class, property.getValue());
      uiContext.setAttribute(ConstraintType.class, constraintType);
      uiContext.setAttribute("onAccessor", onAccessor.getValue());
      if (constraintType == CoreConstraints.VALID)
      {
         return null;
      }
      else
      {
         return Results.navigateTo(GenerateConstraintWizardStep.class);
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

}
