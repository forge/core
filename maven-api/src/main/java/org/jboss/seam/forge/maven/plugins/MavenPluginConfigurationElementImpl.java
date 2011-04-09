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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginConfigurationElementImpl implements MavenPluginConfigurationElement
{
   private String name;
   private String text;
   private List<MavenPluginElement> children = new ArrayList<MavenPluginElement>();


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
      return false;
   }

   @Override public String getText()
   {
      return text;
   }

   public void addChild(MavenPluginElement element)
   {
      children.add(element);
   }


   @Override public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("<").append(name).append(">");
      for (MavenPluginElement child : children)
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
}
