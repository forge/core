/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.plugins;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public interface ConfigurationElement extends PluginElement
{
   String getName();

   boolean isPlugin();

   boolean hasChildren();

   String getText();

   /**
    * Returns an immutable map of the element attributes
    */
   Map<String, String> getAttributes();

   List<PluginElement> getChildren();

   /**
    * Checks if a child element is present with the given content.
    * 
    * @param content The content to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return True if an element was found containing the given content
    */
   boolean hasChildByContent(String content, boolean directChildsOnly);

   /**
    * Checks if a child element is present with the given content. Also search indirect child elements.
    * 
    * @param content The content to filter on.
    * @return True if an element was found containing the given content
    * @see ConfigurationElement#hasChildByContent(String, boolean)
    */
   boolean hasChildByContent(String content);

   /**
    * Returns the child element that contains the given content.
    * 
    * @param content The content to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not
    *         found.
    */
   ConfigurationElement getChildByContent(String content, boolean directChildsOnly);

   /**
    * Returns the child element that contains the given content. Also search indirect child elements.
    * 
    * @param content The content to filter on.
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not
    *         found.
    * @see ConfigurationElement#getChildByContent(String, boolean)
    */
   ConfigurationElement getChildByContent(String content);

   /**
    * Checks if the child element that has the given name exists.
    * 
    * @param name The element name to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not
    *         found.
    */
   boolean hasChildByName(String name, boolean directChildsOnly);

   /**
    * Checks if the child element that has the given name exists. Also search indirect child elements.
    * 
    * @param name The element name to filter on.
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not
    *         found.
    */
   boolean hasChildByName(String name);

   /**
    * Returns the child element that has the given name exists.
    * 
    * @param name The element name to filter on.
    * @param directChildsOnly True if only direct elements should be matched
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not
    *         found.
    */
   ConfigurationElement getChildByName(String name, boolean directChildsOnly);

   /**
    * Checks if the child element that has the given name exists. Also search indirect child elements.
    * 
    * @param name The element name to filter on.
    * @return The element found, or raises an {@link ConfigurationElementNotFoundException} if the element was not
    *         found.
    */
   ConfigurationElement getChildByName(String name);

}
