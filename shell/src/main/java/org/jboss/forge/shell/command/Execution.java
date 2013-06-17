/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.constraint.ConstraintEnforcer;
import org.jboss.forge.shell.constraint.ConstraintException;
import org.jboss.forge.shell.events.CommandExecuted;
import org.jboss.forge.shell.events.CommandExecuted.Status;
import org.jboss.forge.shell.events.CommandMissing;
import org.jboss.forge.shell.events.CommandVetoed;
import org.jboss.forge.shell.events.PreCommandExecution;
import org.jboss.forge.shell.exceptions.CommandExecutionException;
import org.jboss.forge.shell.plugins.AliasLiteral;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.Enums;
import org.mvel2.DataConversion;
import org.mvel2.util.ParseTools;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
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

   public void verifyConstraints(final Shell shell)
   {
      ConstraintEnforcer enforcer = new ConstraintEnforcer();
      if (command != null)
      {
         try
         {
            // TODO this is where more complex constraints could be handled on individual commands
            if (!command.isSetup())
               enforcer.verifyAvailable(shell.getCurrentProject(), command.getParent());
         }
         catch (ConstraintException e)
         {
            throw new CommandExecutionException(command, e);
         }
      }
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
               else if (parmTypes[i].isArray() && parmTypes[i].getComponentType().isEnum())
               {
                  Object[] array = (Object[]) parameterArray[i];
                  if (array != null)
                  {
                     Object enums = Array.newInstance(parmTypes[i].getComponentType(), array.length);
                     for (int ctr = 0; ctr < array.length; ctr++)
                     {
                        Array.set(enums, ctr, Enums.valueOf(parmTypes[i].getComponentType(), array[ctr]));
                     }
                     paramStaging[i] = enums;
                  }
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

               Status status = Status.FAILURE;
               ClassLoader current = Thread.currentThread().getContextClassLoader();
               Map<Object, Object> executionContext = new HashMap<Object, Object>();
               boolean vetoed = false;
               try
               {
                  Thread.currentThread().setContextClassLoader(plugin.getClass().getClassLoader());
                  PreCommandExecution event = new PreCommandExecution(command, originalStatement, parameterArray,
                           executionContext);
                  manager.fireEvent(event, new Annotation[0]);
                  vetoed = event.isVetoed();
                  if (!vetoed)
                  {
                     command.getMethod().invoke(plugin, paramStaging);
                     status = Status.SUCCESS;
                  }
               }
               catch (Exception e)
               {
                  throw new CommandExecutionException(command, e);
               }
               finally
               {
                  Thread.currentThread().setContextClassLoader(current);
                  if (vetoed)
                  {
                     manager.fireEvent(new CommandVetoed(command, parameterArray, originalStatement, executionContext));
                  }
                  else
                  {
                     manager.fireEvent(new CommandExecuted(status, command, originalStatement, parameterArray,
                              executionContext));
                  }
                  executionContext.clear();
               }
            }
         }
      }
      else
      {
         manager.fireEvent(new CommandMissing(originalStatement, parameterArray));
      }

   }

   private static boolean isBooleanOption(final Class<?> type)
   {
      return ParseTools.unboxPrimitive(type) == boolean.class;
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
