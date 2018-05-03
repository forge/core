/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.addon.ui.controller.CommandController;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CacheCommandControllerDecorator extends AbstractCommandControllerDecorator
{
   private final Map<String, Boolean> state = new ConcurrentHashMap<>(4);

   public CacheCommandControllerDecorator(CommandController controller)
   {
      super(controller);
   }

   @Override
   public boolean canExecute()
   {
      return state.computeIfAbsent("canExecute", (k) -> super.canExecute());
   }

   @Override
   public boolean hasInput(String inputName)
   {
      return state.computeIfAbsent("hasInput", (k) -> super.hasInput(inputName));
   }

   @Override
   public boolean isEnabled()
   {
      return state.computeIfAbsent("isEnabled", k -> super.isEnabled());
   }

   @Override
   public boolean isValid()
   {
      return state.computeIfAbsent("isValid", k -> super.isValid());
   }

   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      super.setValueFor(inputName, value);
      state.clear();
      return this;
   }

}