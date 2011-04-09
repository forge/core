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

package org.jboss.seam.forge.maven.plugins;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginConfigurationImpl implements MavenPluginConfiguration
{
   private Xpp3Dom configuration;
   private List<MavenPluginConfigurationElement> configurationElements = new ArrayList<MavenPluginConfigurationElement>();

   public MavenPluginConfigurationImpl()
   {
      configuration = new Xpp3Dom("configuration");
   }

   public MavenPluginConfigurationImpl(Xpp3Dom configXml)
   {
      this.configuration = configXml;
      if (configuration != null)
      {

         for (Xpp3Dom xpp3Dom : configuration.getChildren())
         {
            MavenPluginConfigurationElementBuilder builder = MavenPluginConfigurationElementBuilder.create()
                    .setName(xpp3Dom.getName()).setText(xpp3Dom.getValue());
            addChilds(xpp3Dom, builder);
            configurationElements.add(builder);

         }

      }
   }

   @Override public boolean hasConfigurationElement(String configElement)
   {

      for (MavenPluginConfigurationElement configurationElement : configurationElements)
      {
         if (configurationElement.getName().equals(configElement))
         {
            return true;
         }
      }

      return false;
   }

   @Override public List<MavenPluginConfigurationElement> listConfigurationElements()
   {
      return configurationElements;
   }

   private void addChilds(Xpp3Dom xpp3Dom, MavenPluginConfigurationElementBuilder builder)
   {
      builder.setText(xpp3Dom.getValue());

      for (Xpp3Dom child : xpp3Dom.getChildren())
      {

         MavenPluginConfigurationElementBuilder elementBuilder = builder.addChild(child.getName());
         addChilds(child, elementBuilder);

      }
   }

   @Override public MavenPluginConfiguration addConfigurationElement(MavenPluginConfigurationElement element)
   {

      configurationElements.add(element);


      return this;
   }


   @Override public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("<configuration>");

      for (MavenPluginConfigurationElement configurationElement : configurationElements)
      {
         b.append(configurationElement.toString());
      }

      b.append("</configuration>");
      return b.toString();
   }
}
