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

import java.util.List;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public interface ConfigurationElement extends PluginElement
{
   String getName();

   boolean isPlugin();

   boolean hasChilderen();

   String getText();

   List<PluginElement> getChildren();

   /**
    * Checks if a child element is present with the given content.
    * @param content The content to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return True if an element was found containing the given content
    */
   boolean hasChildByContent(String content, boolean directChildsOnly);

   /**
    * Checks if a child element is present with the given content. Also search indirect child elements.
    * @param content The content to filter on.
    * @return True if an element was found containing the given content
    * @see ConfigurationElement#hasChildByContent(String, boolean)
    */
   boolean hasChildByContent(String content);

   /**
    * Returns the child element that contains the given content.
    * @param content The content to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not found.
    */
   ConfigurationElement getChildByContent(String content, boolean directChildsOnly);

   /**
    * Returns the child element that contains the given content. Also search indirect child elements.
    * @param content The content to filter on.
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not found.
    * @see ConfigurationElement#getChildByContent(String, boolean)
    */
   ConfigurationElement getChildByContent(String content);

   /**
    * Checks if the child element that has the given name exists.
    * @param name The element name to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not found.
    */
   boolean hasChildByName(String name, boolean directChildsOnly);

   /**
    * Checks if the child element that has the given name exists. Also search indirect child elements.
    * @param name The element name to filter on.
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not found.
    */
   boolean hasChildByName(String name);

   /**
    * Returns the child element that has the given name exists.
    * @param name The element name to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not found.
    */
   ConfigurationElement getChildByName(String name, boolean directChildsOnly);


   /**
    * Checks if the child element that has the given name exists. Also search indirect child elements.
    * @param name The element name to filter on.
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not found.
    */
   ConfigurationElement getChildByName(String name);

}
