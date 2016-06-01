/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.javaee.validation.ConstraintOperations;
import org.jboss.forge.addon.javaee.validation.ConstraintType;
import org.jboss.forge.addon.javaee.validation.CoreConstraints;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Property;
import org.jboss.forge.roaster.model.source.PropertySource;

public class ValidationSelectFieldWizardStep extends AbstractJavaEECommand implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "On Property", description = "The property on which the constraint applies", required = true, type = InputType.DROPDOWN)
   private UISelectOne<PropertySource<?>> onProperty;

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
      builder.add(onProperty).add(constraint).add(onAccessor);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), ValidationSelectFieldWizardStep.class)
               .name("Select constrained field")
               .description("Select the property you wish to constraint");
   }

   private void setupProperty(final UIContext context) throws Exception
   {
      onProperty.setItemLabelConverter(new Converter<PropertySource<?>, String>()
      {
         @Override
         public String convert(PropertySource<?> source)
         {
            return (source == null) ? null : source.getName();
         }
      });
      onProperty.setValueChoices(new Callable<Iterable<PropertySource<?>>>()
      {
         @Override
         @SuppressWarnings("unchecked")
         public Iterable<PropertySource<?>> call() throws Exception
         {
            JavaResource selectedResource = (JavaResource) context.getAttributeMap().get(JavaResource.class);
            JavaClass<?> javaClass = selectedResource.getJavaType();
            return (Iterable<PropertySource<?>>) javaClass.getProperties();
         }
      });
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
            Property<?> value = onProperty.getValue();
            return value == null ? Boolean.FALSE : value.isAccessible();
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
         Result result = constraintOperations.addValidConstraint(project, onProperty.getValue(), onAccessor.getValue());
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
      Map<Object, Object> attributeMap = uiContext.getAttributeMap();
      attributeMap.put(PropertySource.class, onProperty.getValue());
      attributeMap.put(ConstraintType.class, constraintType);
      attributeMap.put("onAccessor", onAccessor.getValue());
      if (constraintType == CoreConstraints.VALID)
      {
         return null;
      }
      else
      {
         return Results.navigateTo(ValidationGenerateConstraintWizardStep.class);
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

}
