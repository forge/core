/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
               try
               {
                  Thread.currentThread().setContextClassLoader(plugin.getClass().getClassLoader());
                  command.getMethod().invoke(plugin, paramStaging);
                  status = Status.SUCCESS;
               }
               catch (Exception e)
               {
                  throw new CommandExecutionException(command, e);
               }
               finally
               {
                  Thread.currentThread().setContextClassLoader(current);
                  manager.fireEvent(new CommandExecuted(status, command, originalStatement, parameterArray), new Annotation[] {});
               }
            }
         }
      }
      else
      {
         manager.fireEvent(new CommandExecuted(Status.MISSING, command, originalStatement, parameterArray), new Annotation[] {});
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
