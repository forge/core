/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.impl.input.InputComponentProducer;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Predicate;

/**
 * This class acts as an adapter to the UI API for methods with the annotation @Command
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class AnnotationCommandAdapter implements UICommand
{
   private final List<InputComponent<?, ?>> inputs = new ArrayList<>();
   private final InputComponentProducer factory;
   private final Method method;
   private final Object instance;
   private final List<Predicate<UIContext>> enabledPredicates;

   public AnnotationCommandAdapter(Method method, Object instance, InputComponentProducer factory,
            List<Predicate<UIContext>> enabledPredicates)
   {
      this.method = method;
      this.instance = instance;
      this.factory = factory;
      this.enabledPredicates = enabledPredicates;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      Command ann = method.getAnnotation(Command.class);
      String name = ann.value();
      if (name.isEmpty())
      {
         name = method.getName();
      }
      return Metadata.forCommand(method.getDeclaringClass()).name(name).description(ann.help())
               .category(Categories.create(ann.categories()));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      boolean enabled = true;
      for (int i = 0; enabled && (i < enabledPredicates.size()); i++)
      {
         enabled &= enabledPredicates.get(i).accept(context);
      }
      return enabled;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      int position = 0;
      for (Class<?> parameterType : method.getParameterTypes())
      {
         if (!ReservedParameters.isReservedParameter(parameterType))
         {
            Option option = null;
            for (Annotation annotation : method.getParameterAnnotations()[position])
            {
               if (annotation instanceof Option)
               {
                  option = (Option) annotation;
               }
            }

            if (option != null)
            {
               char shortName = option.shortName();
               String paramName = (option.value().isEmpty()) ? "param" + position : option.value();
               InputComponent<?, ?> input;
               if (Iterable.class.isAssignableFrom(parameterType))
               {
                  // TODO: UIInputMany or UISelectMany ?
                  input = factory.createInputMany(paramName, shortName, parameterType);
               }
               else if (parameterType.isEnum() || Boolean.class == parameterType)
               {
                  input = factory.createSelectOne(paramName, shortName, parameterType);
                  factory.setupSelectComponent((SelectComponent<?, ?>) input);
               }
               else
               {
                  input = factory.createInput(paramName, shortName, parameterType);
               }
               factory.preconfigureInput(input, option);
               builder.add(input);
               inputs.add(input);
            }
         }
         position++;
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      // TODO
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      List<Object> args = new ArrayList<>();
      int idx = 0;
      for (Class<?> type : method.getParameterTypes())
      {
         Object value = null;
         if (ReservedParameters.isReservedParameter(type))
         {
            value = ReservedParameters.getReservedParameter(context, type);
         }
         else
         {
            Option option = null;
            for (Annotation annotation : method.getParameterAnnotations()[args.size()])
            {
               if (annotation instanceof Option)
               {
                  option = (Option) annotation;
               }
            }

            if (option != null)
            {
               value = InputComponents.getValueFor(inputs.get(idx));
               idx++;
            }
         }
         args.add(value);
      }
      Object result = null;
      try
      {
         result = method.invoke(instance, args.toArray(new Object[args.size()]));
      }
      catch (InvocationTargetException ie)
      {
         throw (Exception) ie.getCause();
      }
      if (result == null)
      {
         return Results.success();
      }
      else if (result instanceof Result)
      {
         return (Result) result;
      }
      else
      {
         return Results.success(result.toString());
      }
   }
}
