/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.input;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.EnumSet;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.impl.facets.HintsFacetImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

/**
 * Produces UIInput objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@Singleton
@SuppressWarnings("unchecked")
public class InputComponentProducer implements InputComponentFactory
{
   private final AddonRegistry addonRegistry;
   private final Environment environment;
   private final ConverterFactory converterFactory;
   private final Imported<InputComponentInjectionEnricher> enrichers;

   @Inject
   public InputComponentProducer(AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
      this.environment = addonRegistry.getServices(Environment.class).get();
      this.converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
      this.enrichers = addonRegistry.getServices(InputComponentInjectionEnricher.class);
   }

   @Produces
   public <T> UISelectOne<T> produceSelectOne(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();
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
      WithAttributes withAttributes = injectionPoint.getAnnotated().getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UISelectOne<T> input = createSelectOne(paramName, shortName, valueType);
      setupSelectComponent(input);
      preconfigureInput(input, withAttributes);
      for (InputComponentInjectionEnricher enricher : enrichers)
      {
         enricher.enrich(injectionPoint, input);
      }
      return input;
   }

   @Produces
   public <T> UISelectMany<T> produceSelectMany(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();
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

      WithAttributes withAttributes = injectionPoint.getAnnotated().getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UISelectMany<T> input = createSelectMany(paramName, shortName, valueType);
      setupSelectComponent(input);
      preconfigureInput(input, withAttributes);
      for (InputComponentInjectionEnricher enricher : enrichers)
      {
         enricher.enrich(injectionPoint, input);
      }
      return input;
   }

   @Produces
   public <T> UIInput<T> produceInput(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();

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
      WithAttributes withAttributes = injectionPoint.getAnnotated().getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UIInput<T> input = createInput(paramName, shortName, valueType);
      preconfigureInput(input, withAttributes);
      for (InputComponentInjectionEnricher enricher : enrichers)
      {
         enricher.enrich(injectionPoint, input);
      }
      return input;
   }

   @Produces
   public <T> UIInputMany<T> produceInputMany(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();
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

      WithAttributes withAttributes = injectionPoint.getAnnotated().getAnnotation(WithAttributes.class);
      String paramName = (withAttributes == null || withAttributes.name().trim().isEmpty()) ? name
               : withAttributes.name();
      char shortName = (withAttributes == null) ? InputComponents.DEFAULT_SHORT_NAME : withAttributes.shortName();
      UIInputMany<T> input = createInputMany(paramName, shortName, valueType);
      preconfigureInput(input, withAttributes);
      for (InputComponentInjectionEnricher enricher : enrichers)
      {
         enricher.enrich(injectionPoint, input);
      }
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

   @Override
   public <T> UIInput<T> createInput(String name, char shortName, Class<T> valueType)
   {
      UIInputImpl<T> input = new UIInputImpl<>(name, shortName, valueType);
      configureRequiredFacets(input);
      if (Boolean.class.equals(valueType) || boolean.class.equals(valueType))
         ((UIInput<Boolean>) input).setDefaultValue(Boolean.FALSE);
      return input;
   }

   @Override
   public <T> UIInputMany<T> createInputMany(String name, char shortName, Class<T> valueType)
   {
      UIInputManyImpl<T> input = new UIInputManyImpl<>(name, shortName, valueType);
      configureRequiredFacets(input);
      return input;
   }

   @Override
   public <T> UISelectOne<T> createSelectOne(String name, char shortName, Class<T> valueType)
   {
      UISelectOneImpl<T> input = new UISelectOneImpl<>(name, shortName, valueType);
      configureRequiredFacets(input);
      return input;
   }

   @Override
   public <T> UISelectMany<T> createSelectMany(String name, char shortName, Class<T> valueType)
   {
      UISelectManyImpl<T> input = new UISelectManyImpl<>(name, shortName, valueType);
      configureRequiredFacets(input);
      return input;
   }

   /**
    * Pre-configure input based on WithAttributes info if annotation exists
    */
   public void preconfigureInput(InputComponent<?, ?> input, WithAttributes atts)
   {
      if (atts != null)
      {
         input.setEnabled(atts.enabled());
         input.setLabel(atts.label());
         input.setRequired(atts.required());
         input.setRequiredMessage(atts.requiredMessage());
         input.setDescription(atts.description());

         // Set input type
         if (!InputType.DEFAULT.equals(atts.type()))
         {
            input.getFacet(HintsFacet.class).setInputType(atts.type());
         }

         // Set Default Value
         if (!atts.defaultValue().isEmpty())
         {
            InputComponents.setDefaultValueFor(converterFactory, (InputComponent<?, Object>) input,
                     atts.defaultValue());
         }
      }
   }

   public void preconfigureInput(InputComponent<?, ?> input, Option option)
   {
      if (option != null)
      {
         input.setEnabled(option.enabled());
         if (option.label().isEmpty())
         {
            input.setLabel(option.value());
         }
         else
         {
            input.setLabel(option.label());
         }
         input.setRequired(option.required());
         input.setRequiredMessage(option.requiredMessage());
         input.setDescription(option.description());

         // Set input type
         if (!InputType.DEFAULT.equals(option.type()))
         {
            input.getFacet(HintsFacet.class).setInputType(option.type());
         }

         // Set Default Value
         if (!option.defaultValue().isEmpty())
         {
            InputComponents.setDefaultValueFor(converterFactory, (InputComponent<?, Object>) input,
                     option.defaultValue());
         }
      }
   }

   @SuppressWarnings("rawtypes")
   public void setupSelectComponent(SelectComponent selectComponent)
   {
      Class<?> valueType = selectComponent.getValueType();
      Iterable<?> choices = null;
      if (valueType.isEnum())
      {
         // Auto-populate Enums on SelectComponents
         Class<? extends Enum> enumClass = valueType.asSubclass(Enum.class);
         choices = EnumSet.allOf(enumClass);
      }
      else if (Boolean.class == valueType || boolean.class == valueType)
      {
         choices = Arrays.asList(Boolean.TRUE, Boolean.FALSE);
      }
      else
      {
         // Auto-populate Exported values on SelectComponents
         choices = addonRegistry.getServices(valueType);
      }
      selectComponent.setValueChoices(choices);
   }

   private void configureRequiredFacets(InputComponent<?, ?> input)
   {
      HintsFacetImpl hintsFacet = new HintsFacetImpl(input, environment);
      input.install(hintsFacet);
   }

   @Override
   public <T> UIInput<T> createInput(String name, Class<T> valueType)
   {
      return createInput(name, InputComponents.DEFAULT_SHORT_NAME, valueType);
   }

   @Override
   public <T> UIInputMany<T> createInputMany(String name, Class<T> valueType)
   {
      return createInputMany(name, InputComponents.DEFAULT_SHORT_NAME, valueType);
   }

   @Override
   public <T> UISelectOne<T> createSelectOne(String name, Class<T> valueType)
   {
      return createSelectOne(name, InputComponents.DEFAULT_SHORT_NAME, valueType);
   }

   @Override
   public <T> UISelectMany<T> createSelectMany(String name, Class<T> valueType)
   {
      return createSelectMany(name, InputComponents.DEFAULT_SHORT_NAME, valueType);
   }
}
