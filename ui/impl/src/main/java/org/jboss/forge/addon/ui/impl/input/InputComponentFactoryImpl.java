/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.input;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

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
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
@SuppressWarnings("unchecked")
public class InputComponentFactoryImpl implements InputComponentFactory
{

   private final AddonRegistry addonRegistry;
   private final Environment environment;
   private final ConverterFactory converterFactory;

   @Inject
   public InputComponentFactoryImpl(AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
      this.environment = addonRegistry.getServices(Environment.class).get();
      this.converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
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
      setupSelectComponent(input);
      configureRequiredFacets(input);
      return input;
   }

   @Override
   public <T> UISelectMany<T> createSelectMany(String name, char shortName, Class<T> valueType)
   {
      UISelectManyImpl<T> input = new UISelectManyImpl<>(name, shortName, valueType);
      setupSelectComponent(input);
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
         input.setDeprecated(atts.deprecated());
         input.setDeprecatedMessage(atts.deprecatedMessage());

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
         // Set Note
         if (!atts.note().isEmpty())
         {
            input.setNote(atts.note());
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
         input.setDeprecated(option.deprecated());
         input.setDeprecatedMessage(option.deprecatedMessage());

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
         // Set Note
         if (!option.note().isEmpty())
         {
            input.setNote(option.note());
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
      else if (String.class == valueType || Integer.class == valueType)
      {
         // Optimization: Don't attempt to lookup for java.lang types
         choices = Collections.emptyList();
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
}
