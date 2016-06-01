/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

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
