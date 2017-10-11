/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.convert.CompositeConverter;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.HasCompleter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.furnace.util.Sets;
import org.jboss.forge.furnace.util.Strings;

/**
 * Utilities for {@link InputComponent} objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class InputComponents
{
   public static final char DEFAULT_SHORT_NAME = ' ';

   private static final String COLON = ":";

   /**
    * @return the {@link InputType} object associated to this {@link InputComponent}
    */
   public static String getInputType(InputComponent<?, ?> input)
   {
      String result = InputType.DEFAULT;
      for (Facet f : input.getFacets())
      {
         if (HintsFacet.class.isInstance(f))
         {
            result = ((HintsFacet) f).getInputType();
            break;
         }
      }
      // FIXME: The following code does NOT work when called from Eclipse. Could it be a bug in CLAC ?
      // if (input.hasFacet(HintsFacet.class))
      // {
      // HintsFacet facet = input.getFacet(HintsFacet.class);
      // result = facet.getInputType();
      // }
      return result;
   }

   /**
    * Returns the value stored in this {@link InputComponent}. <code>null</code> if the component is null
    */
   public static Object getValueFor(InputComponent<?, ?> component)
   {
      return (component == null) ? null : component.getValue();
   }

   /**
    * Sets the value in the provided {@link InputComponent}, making any necessary conversions
    *
    * @param component
    * @param value
    */
   public static void setValueFor(final ConverterFactory converterFactory, final InputComponent<?, ?> component,
            final Object value)
   {
      if (component instanceof SingleValued)
      {
         setSingleInputValue(converterFactory, component, value, false);
      }
      else if (component instanceof ManyValued)
      {
         setManyInputValue(converterFactory, component, value, false);
      }
   }

   /**
    * Sets the default value in the provided {@link InputComponent}, making any necessary conversions
    *
    * @param component
    * @param value
    */
   public static void setDefaultValueFor(final ConverterFactory converterFactory,
            final InputComponent<?, Object> component,
            final Object value)
   {
      if (component instanceof SingleValued)
      {
         setSingleInputValue(converterFactory, component, value, true);
      }
      else if (component instanceof ManyValued)
      {
         setManyInputValue(converterFactory, component, value, true);
      }
   }

   private static void setSingleInputValue(final ConverterFactory converterFactory,
            final InputComponent<?, ?> input, final Object value, boolean defaultValue)
   {
      final Object convertedType;
      if (value != null)
      {
         convertedType = convertToUIInputValue(converterFactory, input, value);
      }
      else
      {
         convertedType = null;
      }
      if (defaultValue)
      {
         ((SingleValued) input).setDefaultValue(convertedType);
      }
      else
      {
         ((SingleValued) input).setValue(convertedType);
      }
   }

   private static void setManyInputValue(final ConverterFactory converterFactory,
            final InputComponent<?, ?> input, Object value, boolean defaultValue)
   {
      final Iterable<Object> convertedValues;
      if (value != null)
      {
         List<Object> convertedValuesList = new ArrayList<>();
         if (value instanceof Iterable && !input.getValueType().isInstance(value))
         {
            for (Object itValue : (Iterable) value)
            {
               Object singleValue = convertToUIInputValue(converterFactory, input, itValue);
               if (singleValue != null)
               {
                  convertedValuesList.add(singleValue);
               }
            }
         }
         else
         {
            Object singleValue = convertToUIInputValue(converterFactory, input, value);
            if (singleValue != null)
            {
               convertedValuesList.add(singleValue);
            }
         }
         convertedValues = convertedValuesList;
      }
      else
      {
         convertedValues = null;
      }
      if (defaultValue)
      {
         ((ManyValued) input).setDefaultValue(convertedValues);
      }
      else
      {
         ((ManyValued) input).setValue(convertedValues);
      }
   }

   /**
    * Returns the converted value that matches the input.
    */
   public static Object convertToUIInputValue(final ConverterFactory converterFactory,
            final InputComponent<?, ?> input, final Object value)
   {
      final Object result;
      Class<Object> sourceType = (Class<Object>) value.getClass();
      Class<Object> targetType = (Class<Object>) input.getValueType();
      if (!targetType.isAssignableFrom(sourceType))
      {
         if (input instanceof SelectComponent)
         {
            SelectComponent<?, Object> selectComponent = (SelectComponent<?, Object>) input;
            Iterable<Object> valueChoices = selectComponent.getValueChoices();
            final Converter<Object, ?> selectConverter;
            if (String.class.isAssignableFrom(sourceType))
            {
               selectConverter = getItemLabelConverter(converterFactory, selectComponent);
            }
            else
            {
               selectConverter = converterFactory.getConverter(targetType, sourceType);
            }
            Object chosenObj = null;
            if (valueChoices != null)
            {
               for (Object valueChoice : valueChoices)
               {
                  Object convertedObj = selectConverter.convert(valueChoice);
                  if (convertedObj.equals(value))
                  {
                     chosenObj = valueChoice;
                     break;
                  }
               }
            }
            result = chosenObj;
         }
         else
         {

            Converter<String, Object> valueConverter = (Converter<String, Object>) input.getValueConverter();
            if (valueConverter != null)
            {
               if (value instanceof String)
               {
                  result = valueConverter.convert((String) value);
               }
               else
               {
                  Converter<Object, String> stringConverter = converterFactory.getConverter(sourceType, String.class);
                  CompositeConverter compositeConverter = new CompositeConverter(stringConverter, valueConverter);
                  result = compositeConverter.convert(value);
               }

            }
            else
            {
               Converter<Object, Object> converter = converterFactory.getConverter(sourceType, targetType);
               result = converter.convert(value);
            }
         }
      }
      else
      {

         Converter<String, Object> valueConverter = (Converter<String, Object>) input.getValueConverter();
         if (valueConverter != null && value instanceof String)
         {
            result = valueConverter.convert((String) value);
         }
         else
         {
            // FORGE-2493: By setting the system property 'org.jboss.forge.ui.select_one_lenient_value' to true will
            // allow UISelectOne to set values outside of its value choices. (pre-2.20.0.Final behavior)
            if (input instanceof SelectComponent && !Boolean.getBoolean("org.jboss.forge.ui.select_one_lenient_value"))
            {
               SelectComponent<?, Object> selectComponent = (SelectComponent<?, Object>) input;
               Set<Object> valueChoices = Sets.toSet(selectComponent.getValueChoices());
               // Check if the value is contained in the valueChoices set
               if (valueChoices != null && valueChoices.contains(value))
               {
                  result = value;
               }
               else
               {
                  // equals()/hashCode may not have been implemented. Trying to compare from the String representation
                  Object chosenObj = null;
                  if (valueChoices != null)
                  {
                     Converter<Object, String> selectConverter = getItemLabelConverter(converterFactory,
                              selectComponent);
                     String valueLabel = selectConverter.convert(value);
                     for (Object valueChoice : valueChoices)
                     {
                        String convertedObj = selectConverter.convert(valueChoice);
                        if (convertedObj.equals(valueLabel))
                        {
                           chosenObj = valueChoice;
                           break;
                        }
                     }
                  }
                  result = chosenObj;
               }
            }
            else
            {
               result = value;
            }
         }
      }
      return result;
   }

   /**
    * Returns if there is a value set for this {@link InputComponent}
    */
   public static boolean hasValue(InputComponent<?, ?> input)
   {
      boolean ret;
      Object value = InputComponents.getValueFor(input);
      if (value == null)
      {
         ret = false;
      }
      else if (value instanceof String && value.toString().isEmpty())
      {
         ret = false;
      }
      else if (!input.getValueType().isInstance(value) && value instanceof Iterable
               && !((Iterable) value).iterator().hasNext())
      {
         ret = false;
      }
      else
      {
         ret = true;
      }
      return ret;
   }

   /**
    * Validate if an required and enabled input has a value. If not, return the error message.
    *
    * @param input
    * @return
    */
   public static String validateRequired(final InputComponent<?, ?> input)
   {
      String requiredMessage = null;
      if (input.isEnabled() && input.isRequired() && !InputComponents.hasValue(input))
      {
         requiredMessage = input.getRequiredMessage();
         if (Strings.isNullOrEmpty(requiredMessage))
         {
            String labelValue = input.getLabel() == null ? input.getName() : input.getLabel();
            // Chop : off
            if (labelValue.endsWith(COLON))
            {
               labelValue = labelValue.substring(0, labelValue.length() - 1);
            }
            requiredMessage = labelValue + " must be specified.";
         }
      }
      return requiredMessage;
   }

   /**
    *
    * Returns the item label converter, that is
    *
    * @param converterFactory May be null
    * @param input
    * @return the item label converter of a {@link SelectComponent} or a {@link Converter} instance from the
    *         {@link ConverterFactory} parameter if not null
    */
   public static <IMPLTYPE, VALUETYPE> Converter<VALUETYPE, String> getItemLabelConverter(
            final ConverterFactory converterFactory,
            final SelectComponent<IMPLTYPE, VALUETYPE> input)
   {
      Converter<VALUETYPE, String> converter = input.getItemLabelConverter();
      if (converter == null && converterFactory != null)
      {
         converter = converterFactory.getConverter(input.getValueType(), String.class);
      }
      return converter;
   }

   /**
    * Returns the label for this component
    *
    * @param input the input component
    * @param addColon should a colon be added in the end of the label ?
    * @return the label with a colon in the end if addColon is true
    */
   public static String getLabelFor(InputComponent<?, ?> input, boolean addColon)
   {
      String label = input.getLabel();
      // If no label was provided, use name
      if (label == null)
      {
         label = input.getName();
      }
      // if a colon is required, add it
      if (addColon && !label.endsWith(COLON))
      {
         label += COLON;
      }
      return label;
   }

   /**
    * Returns the completer associated with this {@link InputComponent} or null if it is not available
    *
    * @param inputComponent
    * @return the {@link UICompleter} associated with this {@link InputComponent} or null if not available or the
    *         {@link InputComponent} does not implement {@link HasCompleter}
    */
   public static <VALUETYPE> UICompleter<VALUETYPE> getCompleterFor(InputComponent<?, VALUETYPE> inputComponent)
   {
      final UICompleter<VALUETYPE> result;
      if (inputComponent instanceof HasCompleter)
      {
         result = ((HasCompleter) inputComponent).getCompleter();
      }
      else
      {
         result = null;
      }
      return result;
   }

   /**
    * Determines whether two possibly-null objects are equal. Returns:
    *
    * <ul>
    * <li>{@code true} if {@code a} and {@code b} are both null.
    * <li>{@code true} if {@code a} and {@code b} are both non-null and they are equal according to
    * {@link Object#equals(Object)}.
    * <li>{@code false} in all other situations.
    * </ul>
    *
    * <p>
    * This assumes that any non-null objects passed to this function conform to the {@code equals()} contract.
    */
   public static boolean areEqual(Object a, Object b)
   {
      return a == b || (a != null && a.equals(b));
   }

   /**
    * Determines whether two iterables contain equal elements in the same order. More specifically, this method returns
    * {@code true} if {@code iterable1} and {@code iterable2} contain the same number of elements and every element of
    * {@code iterable1} is equal to the corresponding element of {@code iterable2}.
    */
   public static boolean areElementsEqual(
            Iterable<?> iterable1, Iterable<?> iterable2)
   {
      if (iterable1 == null || iterable2 == null)
      {
         return false;
      }
      Iterator<?> iterator1 = iterable1.iterator();
      Iterator<?> iterator2 = iterable2.iterator();
      while (iterator1.hasNext())
      {
         if (!iterator2.hasNext())
         {
            return false;
         }
         Object o1 = iterator1.next();
         Object o2 = iterator2.next();
         if (!areEqual(o1, o2))
         {
            return false;
         }
      }
      return !iterator2.hasNext();
   }

}
