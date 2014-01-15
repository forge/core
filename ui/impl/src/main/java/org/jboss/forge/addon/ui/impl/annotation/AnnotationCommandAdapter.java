/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.annotation.handler.EnableCommandHandler;
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
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.util.Metadata;

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
   private final EnableCommandHandler enabledHandler;

   public AnnotationCommandAdapter(Method method, Object instance, InputComponentProducer factory,
            EnableCommandHandler handler)
   {
      this.method = method;
      this.instance = instance;
      this.factory = factory;
      this.enabledHandler = handler;
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
      return Metadata.forCommand(method.getDeclaringClass()).name(name).description(ann.help());
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return enabledHandler.isEnabled(context);
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

            String paramName = (option == null || option.value().isEmpty()) ? "param" + position : option.value();
            InputComponent<?, ?> input;
            if (Iterable.class.isAssignableFrom(parameterType))
            {
               // TODO: UIInputMany or UISelectMany ?
               input = factory.createInputMany(paramName, parameterType);
            }
            else if (parameterType.isEnum() || Boolean.class == parameterType)
            {
               input = factory.createSelectOne(paramName, parameterType);
               factory.setupSelectComponent((SelectComponent<?, ?>) input);
            }
            else
            {
               input = factory.createInput(paramName, parameterType);
            }
            factory.preconfigureInput(input, option);
            builder.add(input);
            inputs.add(input);
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
         Object value;
         if (ReservedParameters.isReservedParameter(type))
         {
            value = ReservedParameters.getReservedParameter(context, type);
         }
         else
         {
            value = InputComponents.getValueFor(inputs.get(idx));
            idx++;
         }
         args.add(value);
      }
      Object result = method.invoke(instance, args.toArray(new Object[args.size()]));
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
