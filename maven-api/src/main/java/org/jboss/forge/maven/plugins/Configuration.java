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
 * A Maven plugin Configuration object.
 * 
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public interface Configuration extends PluginElement
{
   /**
    * Return true if this {@link Configuration} has any elements; otherwise, return false.
    */
   boolean hasConfigurationElements();

   /**
    * Return true if this {@link Configuration} contains the given element; otherwise, return false;
    */
   boolean hasConfigurationElement(String element);

   /**
    * Get the specified {@link ConfigurationElement}, if it exists; otherwise, return null.
    */
   ConfigurationElement getConfigurationElement(String element);

   /**
    * List all {@link ConfigurationElement}s contained in this {@link Configuration}. Returns an empty list if none
    * exist.
    */
   List<ConfigurationElement> listConfigurationElements();

   /**
    * Add a {@link ConfigurationElement} to this {@link Configuration}.
    */
   Configuration addConfigurationElement(ConfigurationElement element);

   /**
    * Remove the specified {@link ConfigurationElement}.
    */
   void removeConfigurationElement(String elementName);

}
