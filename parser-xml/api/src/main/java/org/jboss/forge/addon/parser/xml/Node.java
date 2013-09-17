/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml;

import java.util.List;
import java.util.Map;

/**
 * {@link Node} is a data structure representing a container in a classic tree. May sometimes be synonymous with the
 * term "Element" in XML. It may contain a {@link Map} of attributes ({@link String}s), a reference to a {@link List} of
 * child {@link Node}s, and text data.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface Node
{

   /**
    * Add or override a named attribute.<br/>
    * <br/>
    * value will be converted to String using String.valueOf(value);
    * 
    * @param name The attribute name
    * @param value The given value
    * @return This {@link Node}
    * @see #attribute(String, String)
    */
   Node attribute(final String name, final Object value);

   /**
    * Add or override a named attribute.<br/>
    * 
    * @param name The attribute name
    * @param value The given value
    * @return This {@link Node}
    */
   Node attribute(final String name, final String value);

   /**
    * Get a named attribute.<br/>
    * 
    * @param name The attribute name
    * @return The attribute value or null of none defined.
    */
   String getAttribute(final String name);

   /**
    * Remove a named attribute.<br/>
    * 
    * @param name The attribute name
    * @return The attribute value that was removed, or null if the attribute with the given name was not found.
    * @throws IllegalArgumentException If the name is not specified
    */
   String removeAttribute(final String name) throws IllegalArgumentException;

   /**
    * Get all defined attributes for this Node in an immutable view
    * 
    * @return All defined attributes.
    */
   Map<String, String> getAttributes();

   /**
    * Returns whether or not this {@link Node} represents a comment
    * 
    * @return
    */
   boolean isComment();

   /**
    * Marks this {@link Node} as a comment
    * 
    * @param comment Whether or not this is a comment
    * @return
    * @throws IllegalArgumentException If this node has children
    */
   void setComment(final boolean comment) throws IllegalArgumentException;

   /**
    * Obtains the root {@link Node} for this reference
    * 
    * @return
    */
   Node getRoot();

   /**
    * Returns whether or not this {@link Node} is a root
    * 
    * @return
    */
   boolean isRoot();

   // -------------------------------------------------------------------------------------||
   // Node creation / retrieval -----------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Create a new {@link Node} with given name. <br/>
    * <br/>
    * The new {@link Node} will have this as parent.
    * 
    * @param name The name of the {@link Node}.
    * @return A new child {@link Node}
    * @throws IllegalArgumentException If the name is not specified
    */
   Node createChild(final String name) throws IllegalArgumentException;

   Node createChild(final Pattern... patterns);

   /**
    * Get or create a named child node. <br/>
    * <br/>
    * If a named node is found using {@link #getSingle(String)} it is returned, else a new child node is created.
    * 
    * @param name The child node name.
    * @return The existing node or a new node, never null.
    * @see #getSingle(String)
    * @see #createChild(String)
    * @throws IllegalArgumentException if multiple children with name exists.
    */
   Node getOrCreate(final String name);

   Node getOrCreate(final Pattern... patterns);

   /**
    * Get a single child node.<br/>
    * <br/>
    * If multiple children are found with same name it is considered a IllegalArgumentException.
    * 
    * @param name The child node name
    * @return The named child node or null if non found
    * @throws IllegalArgumentException if multiple children with name exists.
    */
   Node getSingle(final String name);

   Node getSingle(final Pattern... patterns);

   /**
    * Get all children with a specific name.
    * 
    * @param name The child node name.
    * @return All found children, or empty list if none found.
    */
   List<Node> get(final String name);

   /**
    * Get all children matching the specified query.
    * 
    * @param query The query to use for finding relevant child nodes
    * @return All found children, or empty list if none found.
    */
   List<Node> get(final Pattern... patterns);

   /**
    * Add a child to this {@link Node}
    * 
    * @return false if the node contained the given child, true if it was added
    */
   boolean addChild(final Node child);

   /**
    * Remove all child nodes found at the given query.
    * 
    * @return the {@link List} of removed children.
    * @throws IllegalArgumentException If the specified name is not specified
    */
   List<Node> removeChildren(final String name) throws IllegalArgumentException;

   /**
    * Remove all child nodes found at the given {@link Pattern}s.
    * 
    * @return the {@link List} of removed children.
    * @throws IllegalArgumentException If pattern is not specified
    */
   List<Node> removeChildren(final Pattern pattern, final Pattern... patterns);

   /**
    * Remove a single child from this {@link Node}
    * 
    * @return true if this node contained the given child
    */
   boolean removeChild(final Node child);

   /**
    * Remove a single child from this {@link Node}
    * 
    * @return true if this node contained the given child
    * @throws IllegalArgumentException if multiple children with name exist.
    */
   Node removeChild(final String name);

   // -------------------------------------------------------------------------------------||
   // Local data --------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Set the Nodes text body.<br/>
    * <br/>
    * text will be converted to String using String.valueOf(text);
    * 
    * @param text
    * @return
    * @see #text(String)
    */
   Node text(final Object text);

   /**
    * Set the Nodes text body.
    * 
    * @param text The text content
    * @return This
    */
   Node text(final String text);

   /**
    * Get the Nodes text body.
    * 
    * @return Set body or null if none.
    */
   String getText();

   /**
    * Get the text value of the element found at the given query name. If no element is found, or no text exists, return
    * null;
    */
   String getTextValueForPatternName(final String name);

   /**
    * Get the text values of all elements found at the given query name. If no elements are found, or no text exists,
    * return an empty list;
    */
   List<String> getTextValuesForPatternName(final String name);

   /**
    * Get the Nodes name.
    * 
    * @return Given name.
    */
   String getName();

   /**
    * Get the Nodes parent.
    * 
    * @return The given parent or null if root node.
    */
   Node getParent();

   /**
    * Get all the defined children for this node in an immutable view.
    * 
    * @return All children or empty list if none.
    */
   List<Node> getChildren();

}
