/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.container.command.CommandExecuted.CommandStatus;
import org.jboss.forge.container.exception.CommandExecutionException;
import org.jboss.forge.container.meta.CommandMetadata;
import org.jboss.forge.container.meta.OptionMetadata;
import org.jboss.forge.container.plugin.AliasLiteral;
import org.jboss.forge.container.plugin.PipeOut;
import org.jboss.forge.container.plugin.Plugin;
import org.jboss.forge.container.util.Enums;
import org.jboss.forge.container.util.Types;
import org.mvel2.DataConversion;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Execution
{
   private final BeanManager manager;

   private CommandMetadata command;
   private Object[] parameterArray;
   private String originalStatement;

   private boolean scriptOnly;

   @Inject
   public Execution(final BeanManager manager)
   {
      this.manager = manager;
   }

   @SuppressWarnings("unchecked")
   public void perform(final PipeOut pipeOut)
   {
      if (command != null)
      {
         Class<? extends Plugin> pluginType = command.getParent().getType();

         Set<Bean<?>> beans = manager.getBeans(pluginType, new AliasLiteral(command.getParent().getName()));
         Bean<?> bean = manager.resolve(beans);

         Method method = command.getMethod();

         Class<?>[] parmTypes = method.getParameterTypes();
         Object[] paramStaging = new Object[parameterArray.length];

         for (int i = 0; i < parmTypes.length; i++)
         {
            try
            {
               if (parmTypes[i].isEnum())
               {
                  paramStaging[i] = Enums.valueOf(parmTypes[i], parameterArray[i]);
               }
               else
               {
                  paramStaging[i] = DataConversion.convert(parameterArray[i], parmTypes[i]);
                  if (isBooleanOption(parmTypes[i]) && (null == paramStaging[i]))
                  {
                     paramStaging[i] = false;
                  }
               }
            }
            catch (Exception e)
            {
               OptionMetadata option = command.getOptionByAbsoluteIndex(i);
               String name = null;
               if (option.isNamed())
               {
                  name = "--" + option.getName();
               }
               else
               {
                  name = "at index [" + option.getIndex() + "]";
               }
               throw new CommandExecutionException(command, "command option '"
                        + name
                        + "' must be of type '" + parmTypes[i].getSimpleName() + "'", e);
            }
         }

         Plugin plugin;
         if (bean != null)
         {
            CreationalContext<? extends Plugin> context = (CreationalContext<? extends Plugin>) manager
                     .createCreationalContext(bean);
            if (context != null)
            {
               plugin = (Plugin) manager.getReference(bean, pluginType, context);

               CommandStatus status = CommandStatus.FAILURE;
               ClassLoader current = Thread.currentThread().getContextClassLoader();
               try
               {
                  Thread.currentThread().setContextClassLoader(plugin.getClass().getClassLoader());
                  command.getMethod().invoke(plugin, paramStaging);
                  status = CommandStatus.SUCCESS;
               }
               catch (Exception e)
               {
                  throw new CommandExecutionException(command, e);
               }
               finally
               {
                  Thread.currentThread().setContextClassLoader(current);
                  manager.fireEvent(new CommandExecuted(status, command, originalStatement, parameterArray),
                           new Annotation[] {});
               }
            }
         }
      }

   }

   private static boolean isBooleanOption(final Class<?> type)
   {
      return Types.unboxPrimitive(type) == boolean.class;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }

   public void setCommand(final CommandMetadata command)
   {
      this.command = command;
   }

   public boolean isScriptOnly()
   {
      return scriptOnly;
   }

   public void setScriptOnly(final boolean scriptOnly)
   {
      this.scriptOnly = scriptOnly;
   }

   public Object[] getParameterArray()
   {
      return parameterArray;
   }

   public void setParameterArray(final Object... parameters)
   {
      this.parameterArray = parameters;
   }

   public String getOriginalStatement()
   {
      return originalStatement;
   }

   public void setOriginalStatement(final String originalStatement)
   {
      this.originalStatement = originalStatement;
   }

   @Override
   public String toString()
   {
      return "Execution [" + originalStatement + "]";
   }

}
