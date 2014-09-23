/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.plugins;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationElementBuilder implements ConfigurationElement
{
   private ConfigurationElementImpl configurationElement;
   private ConfigurationBuilder configurationBuilder;
   private ConfigurationElementBuilder elementBuilder;

   @Override
   public String getName()
   {
      return configurationElement.getName();
   }

   @Override
   public boolean isPlugin()
   {
      return configurationElement.isPlugin();
   }

   @Override
   public boolean hasChildren()
   {
      return configurationElement.hasChildren();
   }

   @Override
   public String getText()
   {
      return configurationElement.getText();
   }

   @Override
   public Map<String, String> getAttributes()
   {
      return configurationElement.getAttributes();
   }

   public ConfigurationElementBuilder setText(String text)
   {
      configurationElement.setText(text);
      return this;
   }

   public ConfigurationElementBuilder addAttribute(String name, String value)
   {
      configurationElement.getMutableAttributes().put(name, value);
      return this;
   }

   public ConfigurationElementBuilder removeAttribute(String name)
   {
      configurationElement.getMutableAttributes().remove(name);
      return this;
   }

   public ConfigurationElementBuilder addChild(String configElement)
   {
      ConfigurationElementBuilder builder =
               ConfigurationElementBuilder.create(this)
                        .setName(configElement);
      configurationElement.addChild(builder);
      return builder;
   }

   public ConfigurationElementBuilder addChild(PluginElement element)
   {
      configurationElement.addChild(element);
      return this;
   }

   public ConfigurationBuilder getParentPluginConfig()
   {
      return configurationBuilder;
   }

   public ConfigurationElementBuilder getParentElement()
   {
      return elementBuilder;
   }

   private ConfigurationElementBuilder()
   {
      configurationElement = new ConfigurationElementImpl();
   }

   private ConfigurationElementBuilder(ConfigurationBuilder configurationBuilder)
   {
      configurationElement = new ConfigurationElementImpl();
      this.configurationBuilder = configurationBuilder;
   }

   private ConfigurationElementBuilder(ConfigurationElementBuilder elementBuilder)
   {
      configurationElement = new ConfigurationElementImpl();
      this.elementBuilder = elementBuilder;
   }

   public static ConfigurationElementBuilder create()
   {
      return new ConfigurationElementBuilder();
   }

   public static ConfigurationElementBuilder create(ConfigurationBuilder configurationBuilder)
   {
      ConfigurationElementBuilder builder = new ConfigurationElementBuilder(configurationBuilder);
      builder.configurationBuilder = configurationBuilder;
      return builder;
   }

   public static ConfigurationElementBuilder create(ConfigurationElementBuilder elementBuilder)
   {
      ConfigurationElementBuilder builder = new ConfigurationElementBuilder(elementBuilder);
      builder.elementBuilder = elementBuilder;
      return builder;
   }

   public static ConfigurationElementBuilder createFromExisting(ConfigurationElement element)
   {

      if (element instanceof ConfigurationElementBuilder)
      {
         ConfigurationElementBuilder elementBuilder = (ConfigurationElementBuilder) element;
         ConfigurationElementBuilder builder = new ConfigurationElementBuilder(elementBuilder);

         builder.configurationElement.setName(element.getName());
         builder.configurationElement.setText(element.getText());
         builder.configurationElement.setChildren(element.getChildren());
         builder.configurationElement.getMutableAttributes().putAll(element.getAttributes());
         return builder;

      }
      else if (element instanceof ConfigurationElementImpl)
      {
         ConfigurationElementBuilder builder = new ConfigurationElementBuilder();

         builder.configurationElement = (ConfigurationElementImpl) element;
         return builder;
      }
      else
      {
         throw new IllegalArgumentException("Unsupported type: " + element.getClass());
      }
   }

   public ConfigurationElementBuilder setName(String name)
   {
      configurationElement.setName(name);
      return this;
   }

   public ConfigurationElementBuilder createConfigurationElement(String name)
   {
      ConfigurationElementBuilder builder = ConfigurationElementBuilder.create(this);
      builder.setName(name);
      configurationElement.addChild(builder);
      return builder;
   }

   @Override
   public List<PluginElement> getChildren()
   {
      return configurationElement.getChildren();
   }

   @Override
   public boolean hasChildByContent(String content, boolean directChildsOnly)
   {
      return configurationElement.hasChildByContent(content, directChildsOnly);
   }

   @Override
   public boolean hasChildByContent(String content)
   {
      return configurationElement.hasChildByContent(content);
   }

   @Override
   public ConfigurationElement getChildByContent(String content, boolean directChildsOnly)
   {
      return configurationElement.getChildByContent(content, directChildsOnly);
   }

   @Override
   public ConfigurationElement getChildByContent(String content)
   {
      return configurationElement.getChildByContent(content);
   }

   @Override
   public boolean hasChildByName(String name, boolean directChildsOnly)
   {
      return configurationElement.hasChildByName(name, directChildsOnly);
   }

   @Override
   public boolean hasChildByName(String name)
   {
      return configurationElement.hasChildByName(name);
   }

   @Override
   public ConfigurationElement getChildByName(String name, boolean directChildsOnly)
   {
      return configurationElement.getChildByName(name, directChildsOnly);
   }

   @Override
   public ConfigurationElement getChildByName(String name)
   {
      return configurationElement.getChildByName(name);
   }

   @Override
   public String toString()
   {
      return configurationElement.toString();
   }
}
