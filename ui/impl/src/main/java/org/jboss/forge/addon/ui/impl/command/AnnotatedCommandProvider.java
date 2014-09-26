/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.impl.annotation.AnnotationCommandAdapter;
import org.jboss.forge.addon.ui.impl.extension.AnnotatedCommandExtension;
import org.jboss.forge.addon.ui.impl.input.InputComponentProducer;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Implementation of {@link CommandProvider} using CDI {@link Annotation} scanning to locate {@link Command} methods.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class AnnotatedCommandProvider implements CommandProvider
{
   @Inject
   private AddonRegistry registry;

   @Inject
   private InputComponentProducer factory;

   @Inject
   private AnnotatedCommandExtension extension;

   private Logger logger = Logger.getLogger(getClass().getName());

   @Override
   public Iterable<UICommand> getCommands()
   {
      Set<UICommand> result = new HashSet<>();
      for (Method method : extension.getAnnotatedCommandMethods())
      {
         try
         {
            UICommand cmd = createAnnotatedCommand(method);
            if (cmd != null)
            {
               result.add(cmd);
            }
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Error while creating command for method " + method.getName(), e);
         }
      }
      return result;
   }

   private UICommand createAnnotatedCommand(Method method)
   {
      Imported<?> service = registry.getServices(method.getDeclaringClass());
      if (service.isUnsatisfied())
      {
         // Class may not be loaded yet
         logger.log(Level.SEVERE, "Error while finding " + method.getDeclaringClass() + " as a service");
         return null;
      }
      Object instance = service.get();
      Command ann = method.getAnnotation(Command.class);

      List<Predicate<UIContext>> enabledPredicates = new ArrayList<>();
      for (Class<? extends Predicate<UIContext>> type : ann.enabled())
      {
         enabledPredicates.add(registry.getServices(type).get());
      }
      return new AnnotationCommandAdapter(method, instance, factory, enabledPredicates);
   }

   public void addonDestroyed(@Observes PreShutdown shutdown)
   {
      AddonId id = shutdown.getAddon().getId();
      extension.addonDestroyed(id);
   }
}
