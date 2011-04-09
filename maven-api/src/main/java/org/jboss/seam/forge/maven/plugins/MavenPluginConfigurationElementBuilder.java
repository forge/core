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

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginConfigurationElementBuilder implements MavenPluginConfigurationElement
{
   private MavenPluginConfigurationElementImpl configurationElement;
   private MavenPluginConfigurationBuilder configurationBuilder;
   private MavenPluginConfigurationElementBuilder elementBuilder;

   @Override public String getName()
   {
      return configurationElement.getName();
   }

   @Override public boolean isPlugin()
   {
      return configurationElement.isPlugin();
   }

   @Override public boolean hasChilderen()
   {
      return configurationElement.hasChilderen();
   }

   @Override public String getText()
   {
      return configurationElement.getText();
   }

   public MavenPluginConfigurationElementBuilder setText(String text)
   {
      configurationElement.setText(text);
      return this;
   }

   public MavenPluginConfigurationElementBuilder addChild(String configElement)
   {
      MavenPluginConfigurationElementBuilder builder =
              MavenPluginConfigurationElementBuilder.create(this)
                      .setName(configElement);
      configurationElement.addChild(builder);
      return builder;
   }

   public MavenPluginConfigurationElementBuilder addChild(MavenPluginElement element)
   {
      configurationElement.addChild(element);
      return this;
   }


   public MavenPluginConfigurationBuilder getParentPluginConfig()
   {
      return configurationBuilder;
   }

   public MavenPluginConfigurationElementBuilder getParentElement()
   {
      return elementBuilder;
   }

   private MavenPluginConfigurationElementBuilder()
   {
      configurationElement = new MavenPluginConfigurationElementImpl();
   }

   private MavenPluginConfigurationElementBuilder(MavenPluginConfigurationBuilder configurationBuilder)
   {
      configurationElement = new MavenPluginConfigurationElementImpl();
      this.configurationBuilder = configurationBuilder;
   }

   private MavenPluginConfigurationElementBuilder(MavenPluginConfigurationElementBuilder elementBuilder)
   {
      configurationElement = new MavenPluginConfigurationElementImpl();
      this.elementBuilder = elementBuilder;
   }

   public static MavenPluginConfigurationElementBuilder create()
   {
      return new MavenPluginConfigurationElementBuilder();
   }

   public static MavenPluginConfigurationElementBuilder create(MavenPluginConfigurationBuilder configurationBuilder)
   {
      MavenPluginConfigurationElementBuilder builder = new MavenPluginConfigurationElementBuilder(configurationBuilder);
      builder.configurationBuilder = configurationBuilder;
      return builder;
   }

   public static MavenPluginConfigurationElementBuilder create(MavenPluginConfigurationElementBuilder elementBuilder)
   {
      MavenPluginConfigurationElementBuilder builder = new MavenPluginConfigurationElementBuilder(elementBuilder);
      builder.elementBuilder = elementBuilder;
      return builder;
   }

   public MavenPluginConfigurationElementBuilder setName(String name)
   {
      configurationElement.setName(name);
      return this;
   }

   public MavenPluginConfigurationElementBuilder createConfigurationElement(String name)
   {
      MavenPluginConfigurationElementBuilder builder = MavenPluginConfigurationElementBuilder.create(this);
      builder.setName(name);
      configurationElement.addChild(builder);
      return builder;
   }

   @Override public String toString()
   {
      return configurationElement.toString();
   }
}
