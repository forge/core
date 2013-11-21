/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonFilter;
import org.jboss.forge.furnace.lock.LockMode;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ListServicesCommand implements UICommand
{
   private Furnace furnace;

   @Inject
   public ListServicesCommand(Furnace furnace)
   {
      this.furnace = furnace;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("list-services").description("List all available services");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return (context instanceof ShellContext);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public void validate(UIValidationContext context)
   {

   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success(listServices());
   }

   private String listServices() throws IOException
   {
      final StringBuilder builder = new StringBuilder();

      furnace.getLockManager().performLocked(LockMode.READ, new Callable<Void>()
      {

         @Override
         public Void call() throws Exception
         {
            Set<Addon> addons = furnace.getAddonRegistry().getAddons(new AddonFilter()
            {
               @Override
               public boolean accept(Addon addon)
               {
                  return addon.getStatus().isStarted();
               }
            });

            for (Addon addon : addons)
            {
               Set<Class<?>> serviceClasses = addon.getServiceRegistry().getExportedTypes();
               for (Class<?> type : serviceClasses)
               {
                  builder.append(type.getName()).append("\n");
                  for (Method method : type.getMethods())
                  {
                     builder.append("\n\ttype - " + getName(method));
                  }
                  builder.append("\n");
               }
            }

            return null;
         }

      });
      return builder.toString();
   }

   public String getName(Method method)
   {
      String params = "(";
      List<Class<?>> parameters = Arrays.asList(method.getParameterTypes());

      Iterator<Class<?>> iterator = parameters.iterator();
      while (iterator.hasNext())
      {
         Class<?> p = iterator.next();
         params += p.getName();

         if (iterator.hasNext())
         {
            params += ",";
         }
      }

      params += ")";

      String returnType = method.getReturnType().getName() == null ? "void" : method.getReturnType().getName();
      return method.getName() + params + "::" + returnType;
   }

}
