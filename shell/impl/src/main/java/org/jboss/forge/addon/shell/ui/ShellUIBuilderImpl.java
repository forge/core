package org.jboss.forge.addon.shell.ui;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;

public class ShellUIBuilderImpl implements UIBuilder
{
   private final Map<String, InputComponent<?, Object>> components = new LinkedHashMap<String, InputComponent<?, Object>>();
   private ShellContext shellContext;

   public ShellUIBuilderImpl(ShellContext context)
   {
      this.shellContext = context;
   }

   @Override
   public UIContext getUIContext()
   {
      return shellContext;
   }

   @SuppressWarnings("unchecked")
   @Override
   public UIBuilder add(InputComponent<?, ?> input)
   {
      components.put(input.getName(), (InputComponent<?, Object>) input);
      return this;
   }

   public Map<String, InputComponent<?, Object>> getComponentMap()
   {
      return Collections.unmodifiableMap(components);
   }

   public Iterable<InputComponent<?, Object>> getComponents()
   {
      return components.values();
   }

}
