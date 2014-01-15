/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.handler.EnableCommandHandler;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.impl.annotation.AnnotationCommandAdapter;
import org.jboss.forge.addon.ui.impl.extension.AnnotatedCommandExtension;
import org.jboss.forge.addon.ui.impl.input.InputComponentProducer;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.event.PreShutdown;

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

   @Override
   public Iterable<UICommand> getCommands()
   {
      Set<UICommand> result = new HashSet<>();
      for (Method method : extension.getAnnotatedCommandMethods())
      {
         result.add(createAnnotatedCommand(method));
      }
      return result;
   }

   private UICommand createAnnotatedCommand(Method method)
   {
      Object instance = registry.getServices(method.getDeclaringClass()).get();
      Command ann = method.getAnnotation(Command.class);
      EnableCommandHandler handler = registry.getServices(ann.enabledHandler()).get();
      return new AnnotationCommandAdapter(method, instance, factory, handler);
   }

   public void addonInitialized(@Observes PreShutdown shutdown)
   {
      AddonId id = shutdown.getAddon().getId();
      extension.addonDestroyed(id);
   }
}
