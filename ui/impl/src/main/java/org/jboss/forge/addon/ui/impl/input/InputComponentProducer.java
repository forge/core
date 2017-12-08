/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.impl.input.inject.DefaultInputComponentInjectionPoint;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionPoint;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.services.Imported;

/**
 * Produces {@link InputComponent} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@Singleton
@SuppressWarnings("unchecked")
public class InputComponentProducer
{
   @Inject
   private Imported<InputComponentInjectionEnricher> enrichers;

   @Inject
   private InputComponentFactoryImpl inputComponentFactory;

   @Produces
   @Dependent
   public <T> UISelectOne<T> produceSelectOne(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Annotated annotated = injectionPoint.getAnnotated();
      Type type = annotated.getBaseType();
      Class<T> valueType;
      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         valueType = resolveRealType(typeArguments[0]);
      }
      else
      {
         // FORGE-1781: Using String if parameter type is not defined
         valueType = (Class<T>) String.class;
      }
      WithAttributes withAttributes = annotated.getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UISelectOne<T> input = inputComponentFactory.createSelectOne(paramName, shortName, valueType);
      input.setDeprecated(annotated.isAnnotationPresent(Deprecated.class));
      inputComponentFactory.preconfigureInput(input, withAttributes);
      enrichInput(injectionPoint, input);
      return input;
   }

   @Produces
   @Dependent
   public <T> UISelectMany<T> produceSelectMany(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Annotated annotated = injectionPoint.getAnnotated();
      Type type = annotated.getBaseType();
      Class<T> valueType;
      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         valueType = resolveRealType(typeArguments[0]);
      }
      else
      {
         // FORGE-1781: Using String if parameter type is not defined
         valueType = (Class<T>) String.class;
      }

      WithAttributes withAttributes = annotated.getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UISelectMany<T> input = inputComponentFactory.createSelectMany(paramName, shortName, valueType);
      input.setDeprecated(annotated.isAnnotationPresent(Deprecated.class));
      inputComponentFactory.preconfigureInput(input, withAttributes);
      enrichInput(injectionPoint, input);
      return input;
   }

   @Produces
   @Dependent
   public <T> UIInput<T> produceInput(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Annotated annotated = injectionPoint.getAnnotated();
      Type type = annotated.getBaseType();

      Class<T> valueType;
      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         valueType = resolveRealType(typeArguments[0]);
      }
      else
      {
         // FORGE-1781: Using String if parameter type is not defined
         valueType = (Class<T>) String.class;
      }
      WithAttributes withAttributes = annotated.getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UIInput<T> input = inputComponentFactory.createInput(paramName, shortName, valueType);
      input.setDeprecated(annotated.isAnnotationPresent(Deprecated.class));
      inputComponentFactory.preconfigureInput(input, withAttributes);
      enrichInput(injectionPoint, input);
      return input;
   }

   @Produces
   @Dependent
   public <T> UIInputMany<T> produceInputMany(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Annotated annotated = injectionPoint.getAnnotated();
      Type type = annotated.getBaseType();
      Class<T> valueType;
      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         valueType = resolveRealType(typeArguments[0]);
      }
      else
      {
         // FORGE-1781: Using String if parameter type is not defined
         valueType = (Class<T>) String.class;
      }

      WithAttributes withAttributes = annotated.getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UIInputMany<T> input = inputComponentFactory.createInputMany(paramName, shortName, valueType);
      input.setDeprecated(annotated.isAnnotationPresent(Deprecated.class));
      inputComponentFactory.preconfigureInput(input, withAttributes);
      enrichInput(injectionPoint, input);
      return input;
   }

   private <T> Class<T> resolveRealType(Type type)
   {
      if (type instanceof ParameterizedType)
      {
         type = ((ParameterizedType) type).getRawType();
      }
      return (Class<T>) type;
   }

   private <T> void enrichInput(InjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      InputComponentInjectionPoint inputInjectionPoint = DefaultInputComponentInjectionPoint.of(injectionPoint);
      for (InputComponentInjectionEnricher enricher : enrichers)
      {
         enricher.enrich(inputInjectionPoint, input);
      }
   }
}
