/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.maven.plugins;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationElementImpl implements ConfigurationElement
{
   private String name;
   private String text;
   private List<PluginElement> children = new ArrayList<PluginElement>();


   public void setName(String name)
   {
      this.name = name;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   @Override public String getName()
   {
      return name;
   }

   @Override public boolean isPlugin()
   {
      return name.equals("plugin");
   }

   @Override public boolean hasChilderen()
   {
      return children != null && children.size() > 0;
   }

   @Override public String getText()
   {
      return text;
   }

   public void addChild(PluginElement element)
   {
      children.add(element);
   }

   public List<PluginElement> getChildren()
   {
      return children;
   }

   public void setChildren(List<PluginElement> children)
   {
      this.children = children;
   }

   @Override public boolean hasChildByContent(String content, boolean directChildsOnly)
   {
      return hasConfigElementByContentRecursive(this, content, FilterType.CONTENT, directChildsOnly);
   }

   @Override public boolean hasChildByContent(String content)
   {
      return hasChildByContent(content, false);
   }

   @Override public ConfigurationElement getChildByContent(String content, boolean directChildsOnly)
   {
      return getConfigElementRecursiveByContent(this, content, FilterType.CONTENT, directChildsOnly);
   }

   @Override public ConfigurationElement getChildByContent(String content)
   {
      return getConfigElementRecursiveByContent(this, content, FilterType.CONTENT, false);
   }

   @Override public boolean hasChildByName(String name, boolean directChildsOnly)
   {
      return hasConfigElementByContentRecursive(this, name, FilterType.CONTENT, directChildsOnly);
   }

   @Override public boolean hasChildByName(String name)
   {
      return hasConfigElementByContentRecursive(this, name, FilterType.NAME, false);
   }

   @Override public ConfigurationElement getChildByName(String name, boolean directChildsOnly)
   {
      return getConfigElementRecursiveByContent(this, name, FilterType.NAME, directChildsOnly);
   }

   @Override public ConfigurationElement getChildByName(String name)
   {
      return getConfigElementRecursiveByContent(this, name, FilterType.NAME, false);
   }

   @Override public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("<").append(name).append(">");
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

   private ConfigurationElement getConfigElementRecursiveByContent(ConfigurationElement parent, String filter, FilterType filterType, boolean directChildsOnly)
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
            } else if (filterType.equals(FilterType.NAME) && filter.equals(element.getName()))
            {
               return element;
            }

            if (!directChildsOnly && element.hasChilderen())
            {
               try
               {
                  return getConfigElementRecursiveByContent(element, filter, filterType, directChildsOnly);
               } catch (ConfigurationElementNotFoundException ex)
               {
                  //Do nothing, first check other childs
               }
            }

         } else
         {
            throw new RuntimeException("Unexpected type " + child.getClass() + " found as a child of " + parent.getName());
         }
      }

      throw new ConfigurationElementNotFoundException(filter);
   }

   private boolean hasConfigElementByContentRecursive(ConfigurationElement configurationElement, String filter, FilterType filterType, boolean directChildsOnly)
   {
      try
      {
         getConfigElementRecursiveByContent(configurationElement, filter, filterType, directChildsOnly);
         return true;
      } catch (ConfigurationElementNotFoundException ex)
      {
         return false;
      }

   }

   private enum FilterType
   {
      NAME, CONTENT
   }
}
