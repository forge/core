/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.UICommandMetadataBase;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ListServicesCommand implements UICommand
{
   private AddonRegistry registry;

   @Inject
   public ListServicesCommand(AddonRegistry registry)
   {
      this.registry = registry;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("list-services", "List all available services");
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
   {
   }

   @Override
   public void validate(UIValidationContext context)
   {

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success(listServices());
   }

   private String listServices() throws IOException
   {
      StringBuilder builder = new StringBuilder();
      Set<Addon> addons = registry.getRegisteredAddons();
      for (Addon addon : addons)
      {
         Set<Class<?>> serviceClasses = addon.getServiceRegistry().getServices();
         for (Class<?> type : serviceClasses)
         {
            builder.append(type.getName()).append("\n");
            for (Method method : type.getMethods())
            {
               builder.append("\n\type - " + getName(method));
            }
            builder.append("\n");
         }
      }

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
