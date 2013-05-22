/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.ui.impl.facets.HintsFacetImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.util.Annotations;

/**
 * Produces UIInput objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class InputComponentProducer
{
   private Environment environment;
   private AddonRegistry addonRegistry;

   @Inject
   public InputComponentProducer(Environment environment, AddonRegistry addonRegistry)
   {
      super();
      this.environment = environment;
      this.addonRegistry = addonRegistry;
   }

   @Produces
   @SuppressWarnings("unchecked")
   public <T> UISelectOne<T> produceSelectOne(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();

      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         Class<T> valueType = (Class<T>) typeArguments[0];
         UISelectOne<T> result = new UISelectOneImpl<T>(name, valueType);
         preconfigureInput(result, injectionPoint.getAnnotated().getAnnotation(WithAttributes.class));
         HintsFacetImpl hintsFacet = new HintsFacetImpl(result, environment);
         result.install(hintsFacet);
         return result;
      }
      else
      {
         throw new IllegalStateException("Cannot inject a generic instance of type " + UIInput.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");
      }
   }

   @Produces
   @SuppressWarnings({ "unchecked" })
   public <T> UISelectMany<T> produceSelectMany(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();

      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         Class<T> valueType = (Class<T>) typeArguments[0];
         UISelectMany<T> result = new UISelectManyImpl<T>(name, valueType);
         preconfigureInput(result, injectionPoint.getAnnotated().getAnnotation(WithAttributes.class));
         HintsFacetImpl hintsFacet = new HintsFacetImpl(result, environment);
         result.install(hintsFacet);
         return result;
      }
      else
      {
         throw new IllegalStateException("Cannot inject a generic instance of type " + UIInput.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");
      }
   }

   @Produces
   @SuppressWarnings({ "unchecked" })
   public <T> UIInput<T> produceInput(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();

      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         Class<T> valueType = (Class<T>) typeArguments[0];
         UIInputImpl<T> result = new UIInputImpl<T>(name, valueType);
         preconfigureInput(result, injectionPoint.getAnnotated().getAnnotation(WithAttributes.class));
         HintsFacetImpl hintsFacet = new HintsFacetImpl(result, environment);
         result.install(hintsFacet);
         return result;
      }
      else
      {
         throw new IllegalStateException("Cannot inject a generic instance of type " + UIInput.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");
      }
   }

   @Produces
   @SuppressWarnings({ "unchecked" })
   public <T> UIInputMany<T> produceInputMany(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getBaseType();

      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         Class<T> valueType = (Class<T>) typeArguments[0];
         UIInputManyImpl<T> result = new UIInputManyImpl<T>(name, valueType);
         preconfigureInput(result, injectionPoint.getAnnotated().getAnnotation(WithAttributes.class));
         HintsFacetImpl hintsFacet = new HintsFacetImpl(result, environment);
         result.install(hintsFacet);
         return result;
      }
      else
      {
         throw new IllegalStateException("Cannot inject a generic instance of type " + UIInput.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");
      }
   }

   /**
    * Pre-configure input based on WithAttributes info if annotation exists
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   private void preconfigureInput(InputComponent<?, ?> input, WithAttributes atts)
   {
      if (atts != null)
      {
         input.setEnabled(atts.enabled());
         input.setLabel(atts.label());
         input.setRequired(atts.required());
         input.setRequiredMessage(atts.requiredMessage());
      }

      if (input instanceof SelectComponent)
      {
         SelectComponent selectComponent = (SelectComponent) input;
         Class<?> valueType = input.getValueType();
         Iterable<?> choices = null;
         // Auto-populate Enums on SelectComponents
         if (valueType.isEnum())
         {
            Class<? extends Enum> enumClass = valueType.asSubclass(Enum.class);
            choices = EnumSet.allOf(enumClass);
         }
         // Auto-populate Exported values on SelectComponents
         else if (Annotations.isAnnotationPresent(valueType, Exported.class))
         {
            List<Object> choiceList = new ArrayList<Object>();
            for (ExportedInstance exportedInstance : addonRegistry.getExportedInstances(valueType))
            {
               choiceList.add(exportedInstance.get());
            }
            choices = choiceList;
         }
         selectComponent.setValueChoices(choices);
      }
   }
}
