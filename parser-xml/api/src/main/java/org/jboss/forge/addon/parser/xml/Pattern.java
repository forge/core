/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml;

import java.util.Map;

import javax.management.Query;

/**
 * A pattern that may be executed as part of a {@link Query} upon a {@link NodeImpl} in a search, or used to define a target
 * {@link NodeImpl} to be created. Value object analogous to XPath patterns; it describes something in the tree structure of
 * {@link NodeImpl}.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public interface Pattern
{

   /**
    * The node name
    * 
    * @return
    */
   String getName();


   /**
    * Add or override a named attribute.<br/>
    * <br/>
    * value will be converted to String using String.valueOf(value);
    * 
    * @param name The attribute name
    * @param value The given value
    * @return This Node
    * @see #attribute(String, String)
    */
   Pattern attribute(final String name, final Object value);

   /**
    * Add or override a named attribute.<br/>
    * 
    * @param name The attribute name
    * @param value The given value
    * @return This Node
    */
   Pattern attribute(final String name, final String value);

   /**
    * Returns an immutable view of all defined attributes for this Node.
    * 
    * @return All defined attributes.
    */
   Map<String, String> getAttributes();

   /**
    * Obtains the value of the named attribute, or null if not present
    * 
    * @param name
    * @return
    * @throws IllegalArgumentException If name is not specified
    */
   String getAttribute(final String name) throws IllegalArgumentException;

   /**
    * Returns true if and only if the specified {@link NodeImpl} values match the data contained in this {@link Pattern}
    * value object
    * 
    * @param node
    * @return
    * @throws IllegalArgumentException If the {@link NodeImpl} is not specified
    */
   boolean matches(final Node node) throws IllegalArgumentException;

   /**
    * Sets the node text to match, returning a reference to this {@link Pattern}
    * 
    * @param text The text value which should be matched
    * @return This {@link Pattern}
    */
   Pattern text(final String text);

   /**
    * Gets the node text to be matched
    * 
    * @return
    */
   String getText();

}
