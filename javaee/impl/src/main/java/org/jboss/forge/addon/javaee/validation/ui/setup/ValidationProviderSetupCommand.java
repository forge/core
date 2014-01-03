/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui.setup;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.javaee.validation.ValidationOperations;
import org.jboss.forge.addon.javaee.validation.provider.ValidationProvider;
import org.jboss.forge.addon.javaee.validation.providers.JavaEEValidatorProvider;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class ValidationProviderSetupCommand extends AbstractJavaEECommand implements UICommand
{
   @Inject
   @WithAttributes(label = "Bean Validation provider", required = true)
   private UISelectOne<ValidationProvider> providers;

   @Inject
   @WithAttributes(label = "Provided by Application Server?")
   private UIInput<Boolean> providedScope;

   @Inject
   @WithAttributes(label = "Message Interpolator Class", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> messageInterpolator;

   @Inject
   @WithAttributes(label = "Traversable Resolver Class", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> traversableResolver;

   @Inject
   @WithAttributes(label = "Constraint Validator Factory Class", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> constraintValidatorFactory;

   @Inject
   private JavaEEValidatorProvider defaultProvider;

   @Inject
   private ValidationOperations validationOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      Metadata metadata = Metadata.from(super.getMetadata(context), getClass());
      return metadata.name("Validation: Setup")
               .description("Setup Bean Validation in your project")
               .category(Categories.create(metadata.getCategory().getName(), "Bean Validation"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return containsProject(context);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      initProviders();

      messageInterpolator.setValidator(new ClassInputValidator(messageInterpolator));
      traversableResolver.setValidator(new ClassInputValidator(traversableResolver));
      constraintValidatorFactory.setValidator(new ClassInputValidator(constraintValidatorFactory));

      providedScope.setDefaultValue(true);
      Callable<Boolean> dependencyNotProvided = new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !providedScope.getValue();
         }
      };

      messageInterpolator.setEnabled(dependencyNotProvided);
      traversableResolver.setEnabled(dependencyNotProvided);
      constraintValidatorFactory.setEnabled(dependencyNotProvided);

      builder.add(providers)
               .add(providedScope)
               .add(messageInterpolator)
               .add(traversableResolver)
               .add(constraintValidatorFactory);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      validationOperations.setup(getSelectedProject(context), providers.getValue(),
               providedScope.getValue(), messageInterpolator.getValue(), traversableResolver.getValue(),
               constraintValidatorFactory.getValue());
      return Results.success("Bean Validation is installed.");
   }

   private void initProviders()
   {
      providers.setItemLabelConverter(new Converter<ValidationProvider, String>()
      {
         @Override
         public String convert(ValidationProvider source)
         {
            return source != null ? source.getName() : null;
         }
      });
      providers.setDefaultValue(defaultProvider);
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
