/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationElementImpl implements ConfigurationElement
{
   private String name;
   private String text;
   private List<PluginElement> children = new ArrayList<>();
   private Map<String, String> attributes;

   public void setName(String name)
   {
      this.name = name;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public boolean isPlugin()
   {
      return name.equals("plugin");
   }

   @Override
   public boolean hasChildren()
   {
      return children != null && children.size() > 0;
   }

   @Override
   public String getText()
   {
      return text;
   }

   public void addChild(PluginElement element)
   {
      children.add(element);
   }

   @Override
   public List<PluginElement> getChildren()
   {
      return children;
   }

   public void setChildren(List<PluginElement> children)
   {
      this.children = children;
   }

   @Override
   public boolean hasChildByContent(String content, boolean directChildsOnly)
   {
      return hasConfigElementByContentRecursive(this, content, FilterType.CONTENT, directChildsOnly);
   }

   @Override
   public boolean hasChildByContent(String content)
   {
      return hasChildByContent(content, false);
   }

   @Override
   public ConfigurationElement getChildByContent(String content, boolean directChildsOnly)
   {
      return getConfigElementRecursiveByContent(this, content, FilterType.CONTENT, directChildsOnly);
   }

   @Override
   public ConfigurationElement getChildByContent(String content)
   {
      return getConfigElementRecursiveByContent(this, content, FilterType.CONTENT, false);
   }

   @Override
   public boolean hasChildByName(String name, boolean directChildsOnly)
   {
      return hasConfigElementByContentRecursive(this, name, FilterType.NAME, directChildsOnly);
   }

   @Override
   public boolean hasChildByName(String name)
   {
      return hasConfigElementByContentRecursive(this, name, FilterType.NAME, false);
   }

   @Override
   public ConfigurationElement getChildByName(String name, boolean directChildsOnly)
   {
      return getConfigElementRecursiveByContent(this, name, FilterType.NAME, directChildsOnly);
   }

   @Override
   public ConfigurationElement getChildByName(String name)
   {
      return getConfigElementRecursiveByContent(this, name, FilterType.NAME, false);
   }

   Map<String, String> getMutableAttributes()
   {
      if (attributes == null)
         attributes = new TreeMap<String, String>();
      return attributes;
   }

   @Override
   public Map<String, String> getAttributes()
   {
      if (attributes == null)
         return Collections.emptyMap();
      else
         return Collections.unmodifiableMap(attributes);
   }

   @Override
   public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("<").append(name);
      if (attributes != null)
         for (Entry<String, String> entry : attributes.entrySet())
         {
            b.append(" ").append(entry.getKey()).append("=").append("\"").append(entry.getValue()).append("\"");
         }
      b.append(">");
      for (PluginElement child : children)
      {
         b.append(child.toString());
      }

      if (text != null)
      {
         b.append(text);
      }

      b.append("</").append(name).append(">");
      return b.toString();
   }

   private ConfigurationElement getConfigElementRecursiveByContent(ConfigurationElement parent, String filter,
            FilterType filterType, boolean directChildsOnly)
   {
      List<PluginElement> children = parent.getChildren();
      for (PluginElement child : children)
      {
         if (child instanceof ConfigurationElement)
         {

            ConfigurationElement element = (ConfigurationElement) child;

            if (filterType.equals(FilterType.CONTENT) && filter.equals(element.getText()))
            {
               return parent;
            }
            else if (filterType.equals(FilterType.NAME) && filter.equals(element.getName()))
            {
               return element;
            }

            if (!directChildsOnly && element.hasChildren())
            {
               try
               {
                  return getConfigElementRecursiveByContent(element, filter, filterType, directChildsOnly);
               }
               catch (ConfigurationElementNotFoundException ex)
               {
                  // Do nothing, first check other childs
               }
            }

         }
         else
         {
            throw new RuntimeException("Unexpected type " + child.getClass() + " found as a child of "
                     + parent.getName());
         }
      }

      throw new ConfigurationElementNotFoundException(filter);
   }

   private boolean hasConfigElementByContentRecursive(ConfigurationElement configurationElement, String filter,
            FilterType filterType, boolean directChildsOnly)
   {
      try
      {
         getConfigElementRecursiveByContent(configurationElement, filter, filterType, directChildsOnly);
         return true;
      }
      catch (ConfigurationElementNotFoundException ex)
      {
         return false;
      }

   }

   private enum FilterType
   {
      NAME, CONTENT
   }
}
