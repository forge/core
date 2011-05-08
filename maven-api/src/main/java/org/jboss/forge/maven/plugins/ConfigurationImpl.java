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

import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationImpl implements Configuration
{
   private Xpp3Dom configuration;
   private List<ConfigurationElement> configurationElements = new ArrayList<ConfigurationElement>();

   public ConfigurationImpl()
   {
      configuration = new Xpp3Dom("configuration");
   }

   public ConfigurationImpl(Xpp3Dom configXml)
   {
      this.configuration = configXml;
      if (configuration != null)
      {

         for (Xpp3Dom xpp3Dom : configuration.getChildren())
         {
            ConfigurationElementBuilder builder = ConfigurationElementBuilder.create()
                    .setName(xpp3Dom.getName()).setText(xpp3Dom.getValue());
            addChilds(xpp3Dom, builder);
            configurationElements.add(builder);

         }

      }
   }

   @Override public ConfigurationElement getConfigurationElement(String configElement)
   {
      for (ConfigurationElement configurationElement : configurationElements)
      {
         if (configurationElement.getName().equals(configElement))
         {
            return configurationElement;
         }
      }

      throw new RuntimeException("Configuration '" + configElement + "' not found");
   }

   @Override public boolean hasConfigurationElement(String configElement)
   {

      for (ConfigurationElement configurationElement : configurationElements)
      {
         if (configurationElement.getName().equals(configElement))
         {
            return true;
         }
      }

      return false;
   }

   @Override public List<ConfigurationElement> listConfigurationElements()
   {
      return configurationElements;
   }

   private void addChilds(Xpp3Dom xpp3Dom, ConfigurationElementBuilder builder)
   {
      builder.setText(xpp3Dom.getValue());

      for (Xpp3Dom child : xpp3Dom.getChildren())
      {

         ConfigurationElementBuilder elementBuilder = builder.addChild(child.getName());
         addChilds(child, elementBuilder);

      }
   }

   @Override public Configuration addConfigurationElement(ConfigurationElement element)
   {

      configurationElements.add(element);

      return this;
   }

   @Override public void removeConfigurationElement(String elementName)
   {
      for (ConfigurationElement configurationElement : configurationElements)
      {
         if (configurationElement.getName().equals(elementName))
         {
            configurationElements.remove(configurationElement);
            break;
         }
      }
   }

   @Override public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("<configuration>");

      for (ConfigurationElement configurationElement : configurationElements)
      {
         b.append(configurationElement.toString());
      }

      b.append("</configuration>");
      return b.toString();
   }
}
