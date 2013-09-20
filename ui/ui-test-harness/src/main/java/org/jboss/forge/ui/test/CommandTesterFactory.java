/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.ui.test.impl.UIContextImpl;
import org.jboss.forge.ui.test.impl.UIProviderImpl;
import org.jboss.forge.ui.test.impl.command.CommandTesterImpl;

/**
 * A factory for {@link CommandTester} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class CommandTesterFactory
{

   @Inject
   private AddonRegistry addonRegistry;

   @Produces
   @SuppressWarnings("rawtypes")
   public CommandTester produceCommandTester(InjectionPoint injectionPoint) throws Exception
   {
      Type type = injectionPoint.getAnnotated().getBaseType();

      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         Class<?> commandClass = (Class<?>) typeArguments[0];
         return CommandTesterFactory.create(commandClass, addonRegistry);
      }
      else
      {
         throw new IllegalStateException("Cannot inject a generic instance of type " + CommandTester.class.getName()
                  + "<?> without specifying concrete generic types at injection point " + injectionPoint + ".");
      }
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static CommandTesterImpl<?> create(Class<?> commandClass, AddonRegistry addonRegistry,
            Resource<?>... initialSelection) throws Exception
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      UISelection<Resource<?>> selection = Selections.from(initialSelection);
      UIContextImpl context = new UIContextImpl(new UIProviderImpl(true), listeners, selection);
      return new CommandTesterImpl(commandClass, addonRegistry, context);
   }
}
