/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.plugins;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ConfigurationImpl implements Configuration
{
   private final Xpp3Dom configuration;
   private final List<ConfigurationElement> configurationElements = new ArrayList<ConfigurationElement>();

   public ConfigurationImpl()
   {
      configuration = new Xpp3Dom("configuration");
   }

   public ConfigurationImpl(final Xpp3Dom configXml)
   {
      this.configuration = configXml;
      if (configuration != null)
      {

         for (Xpp3Dom xpp3Dom : configuration.getChildren())
         {
            ConfigurationElementBuilder builder = ConfigurationElementBuilder.create()
                     .setName(xpp3Dom.getName()).setText(xpp3Dom.getValue());
            addChildren(xpp3Dom, builder);
            configurationElements.add(builder);
         }

      }
   }

   @Override
   public ConfigurationElement getConfigurationElement(final String configElement)
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

   @Override
   public boolean hasConfigurationElement(final String configElement)
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

   @Override
   public boolean hasConfigurationElements()
   {
      return !listConfigurationElements().isEmpty();
   }

   @Override
   public List<ConfigurationElement> listConfigurationElements()
   {
      return configurationElements;
   }

   private void addChildren(final Xpp3Dom xpp3Dom, final ConfigurationElementBuilder builder)
   {
      builder.setText(xpp3Dom.getValue());
      for (String attributeName : xpp3Dom.getAttributeNames())
      {
         String attributeValue = xpp3Dom.getAttribute(attributeName);
         if (attributeValue != null)
            builder.addAttribute(attributeName, attributeValue);
      }

      for (Xpp3Dom child : xpp3Dom.getChildren())
      {
         ConfigurationElementBuilder elementBuilder = builder.addChild(child.getName());
         addChildren(child, elementBuilder);
      }
   }

   @Override
   public Configuration addConfigurationElement(final ConfigurationElement element)
   {

      configurationElements.add(element);

      return this;
   }

   @Override
   public void removeConfigurationElement(final String elementName)
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

   @Override
   public String toString()
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
