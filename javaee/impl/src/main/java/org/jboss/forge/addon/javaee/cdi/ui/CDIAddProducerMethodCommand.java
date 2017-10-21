/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.forge.addon.javaee.cdi.ui.input.Qualifiers;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
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
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;
import org.jboss.forge.roaster.model.util.Strings;
import org.jboss.forge.roaster.model.util.Types;

/**
 * Adds a new CDI producer method.
 *
 * @author Martin Kouba
 */
public class CDIAddProducerMethodCommand extends AbstractMethodCDICommand
{

   @Inject
   @WithAttributes(label = "Return", description = "The return type of the producer method", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> returnType;

   @Inject
   @WithAttributes(label = "Scope", defaultValue = "DEPENDENT")
   private UISelectOne<BeanScope> scope;

   @Inject
   @WithAttributes(label = "Custom Scope Annotation", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> customScopeAnnotation;

   @Inject
   private Qualifiers qualifiers;

   @Inject
   @WithAttributes(label = "Alternative")
   private UIInput<Boolean> alternative;

   @Inject
   @WithAttributes(label = "Named", description = "Defaulted bean name for a producer method")
   private UIInput<Boolean> defaultedName;

   @Inject
   @WithAttributes(label = "Injection Point", description = "Add InjectionPoint metadata parameter")
   private UIInput<Boolean> injectionPoint;

   @Inject
   @WithAttributes(label = "Disposer", description = "Add disposer method stub")
   private UIInput<Boolean> disposer;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: Add Producer Method")
               .description("Adds a new CDI producer method");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      Callable<Boolean> customScopeSelected = new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return scope.getValue() == BeanScope.CUSTOM;
         }
      };
      customScopeAnnotation.setEnabled(customScopeSelected).setRequired(customScopeSelected);
      injectionPoint.setEnabled(() -> scope.getValue() == BeanScope.DEPENDENT);
      builder.add(returnType).add(scope).add(customScopeAnnotation).add(qualifiers).add(alternative).add(defaultedName)
               .add(injectionPoint).add(disposer);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaClassSource javaClass = javaResource.getJavaType();
      MethodSource<JavaClassSource> method = javaClass.addMethod().setVisibility(accessType.getValue())
               .setName(named.getValue())
               .setBody("return null;")
               .setReturnType(returnType.getValue());
      method.addAnnotation(Produces.class);
      BeanScope scopeValue = scope.getValue();
      if (BeanScope.CUSTOM == scopeValue)
      {
         method.addAnnotation(customScopeAnnotation.getValue());
      }
      else
      {
         method.addAnnotation(scopeValue.getAnnotation());
      }
      if (alternative.getValue())
      {
         method.addAnnotation(Alternative.class);
      }
      if (injectionPoint.getValue())
      {
         method.addAnnotation(Named.class);
      }
      for (String qualifier : qualifiers.getValue())
      {
         method.addAnnotation(qualifier);
      }
      if (injectionPoint.isEnabled() && injectionPoint.getValue())
      {
         method.addParameter(InjectionPoint.class, "injectionPoint");
      }

      if (disposer.getValue())
      {
         // Generate disposer method
         ParameterSource<JavaClassSource> disposedParam = javaClass.addMethod().setVisibility(accessType.getValue())
                  .setName(getDisposerMethodName())
                  .setBody("")
                  .setReturnTypeVoid()
                  .addParameter(returnType.getValue(),
                           Strings.uncapitalize(Types.toSimpleName(returnType.getValue())) + "ToDispose");
         disposedParam.addAnnotation(Disposes.class);
         for (String qualifier : qualifiers.getValue())
         {
            disposedParam.addAnnotation(qualifier);
         }
      }

      javaResource.setContents(javaClass);
      return Results.success();
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      if (disposer.getValue())
      {
         JavaResource javaResource = targetClass.getValue();
         if (javaResource != null && javaResource.exists())
         {
            JavaClassSource javaClass;
            try
            {
               javaClass = javaResource.getJavaType();
               String disposerName = getDisposerMethodName();
               if (javaClass.hasMethodSignature(disposerName))
               {
                  validator.addValidationError(disposer, "Disposer method signature already exists: " + disposerName);
               }
            }
            catch (FileNotFoundException ignored)
            {
            }
         }
      }
   }

   @Override
   protected Visibility getDefaultVisibility()
   {
      return Visibility.PACKAGE_PRIVATE;
   }

   @Override
   protected String[] getParamTypes()
   {
      return injectionPoint.getValue() ? new String[] { InjectionPoint.class.getName() } : super.getParamTypes();
   }

   private String getDisposerMethodName()
   {
      return "dispose" + Types.toSimpleName(returnType.getValue());
   }

}
