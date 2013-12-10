/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.context;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIBuilderImpl implements UIBuilder
{

   private final UIContext context;
   private Map<String, InputComponent<?, Object>> inputs = new LinkedHashMap<String, InputComponent<?, Object>>();

   public UIBuilderImpl(UIContext context)
   {
      this.context = context;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @SuppressWarnings("unchecked")
   @Override
   public UIBuilder add(InputComponent<?, ?> input)
   {
      inputs.put(input.getName(), (InputComponent<?, Object>) input);
      return this;
   }

   public Map<String, InputComponent<?, Object>> getInputs()
   {
      return inputs;
   }

}
