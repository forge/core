/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class Node
{
   // -------------------------------------------------------------------------------------||
   // Class Members -----------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   private static final String SPACE = " ";

   /**
    * Used in casting to Pattern arrays from Collections
    */
   private static final Pattern[] PATTERN_CAST = new Pattern[] {};

   // -------------------------------------------------------------------------------------||
   // Instance Members --------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Parent node; null represents that this is the root
    */
   private final Node parent;

   private final List<Node> children = new ArrayList<Node>();

   /**
    * Name of the {@link Node}; may not have spaces
    */
   private final String name;

   /**
    * Attributes of the element
    */
   private final Map<String, String> attributes = new HashMap<String, String>();

   /**
    * CDATA
    */
   private String text;

   /**
    * Denotes whether this is a comment (ie. <!-- X -->)
    */
   private boolean comment;

   // -------------------------------------------------------------------------------------||
   // Constructor -------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Creates a root {@link Node}
    *
    * @param name The name of the node
    */
   public Node(final String name) throws IllegalArgumentException
   {
      this(name, null);
   }

   /**
    * Creates a {@link Node}
    *
    * @param name The name of the node
    * @param parent The parent node. Use null to denote a root.
    * @throws IllegalArgumentException If the name is not specified or contains any space characters
    */
   public Node(final String name, final Node parent) throws IllegalArgumentException
   {
      // Precondition checks
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }
      if (name.contains(SPACE))
      {
         throw new IllegalArgumentException("name may not contain any spaces");
      }

      // Set
      this.name = name;
      this.parent = parent;

      // Set bi-directional relationship if we've got a parent
      if (this.parent != null)
      {
         this.parent.children.add(this);
      }
   }

   // -------------------------------------------------------------------------------------||
   // Attributes -------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

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
   public Node attribute(final String name, final Object value)
   {
      return attribute(name, String.valueOf(value));
   }

   /**
    * Add or override a named attribute.<br/>
    *
    * @param name The attribute name
    * @param value The given value
    * @return This {@link Node}
    */
   public Node attribute(final String name, final String value)
   {
      this.attributes.put(name, value);
      return this;
   }

   /**
    * Get a named attribute.<br/>
    *
    * @param name The attribute name
    * @return The attribute value or null of none defined.
    */
   public String getAttribute(final String name)
   {
      return this.attributes.get(name);
   }

   /**
    * Remove a named attribute.<br/>
    *
    * @param name The attribute name
    * @return The attribute value that was removed, or null if the attribute with the given name was not found.
    * @throws IllegalArgumentException If the name is not specified
    */
   public String removeAttribute(final String name) throws IllegalArgumentException
   {
      // Precondition check
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }

      final String remove = this.attributes.remove(name);
      return remove;
   }

   /**
    * Get all defined attributes for this Node in an immutable view
    *
    * @return All defined attributes.
    */
   public Map<String, String> getAttributes()
   {
      return Collections.unmodifiableMap(attributes);
   }

   /**
    * Returns whether or not this {@link Node} represents a comment
    *
    * @return
    */
   public boolean isComment()
   {
      return comment;
   }

   /**
    * Marks this {@link Node} as a comment
    *
    * @param comment Whether or not this is a comment
    * @return
    * @throws IllegalArgumentException If this node has children
    */
   public void setComment(final boolean comment) throws IllegalArgumentException
   {
      // Cannot have children
      if (this.children.size() > 0)
      {
         throw new IllegalArgumentException("Cannot mark a " + Node.class.getSimpleName()
                  + " with children as a comment");
      }

      // Set
      this.comment = comment;
   }

   /**
    * Obtains the root {@link Node} for this reference
    *
    * @return
    */
   public Node getRoot()
   {
      return this.getRoot(this);
   }

   private Node getRoot(final Node start)
   {
      assert start != null : "node must be specified";
      final Node parent = start.getParent();
      if (parent == null)
      {
         return start;
      }
      // Recurse up
      return this.getRoot(parent);
   }

   /**
    * Returns whether or not this {@link Node} is a root
    *
    * @return
    */
   public boolean isRoot()
   {
      return this.getParent() == null;
   }

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
   public Node createChild(final String name) throws IllegalArgumentException
   {
      // Precondition checks
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }

      // Create
      return createChild(Patterns.from(name));
   }

   Node createChild(final Pattern... patterns)
   {
      return CreateQuery.INSTANCE.execute(this, patterns);
   }

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
   public Node getOrCreate(String name)
   {
      return getOrCreate(Patterns.from(name));
   }

   Node getOrCreate(final Pattern... patterns)
   {
      return GetOrCreateQuery.INSTANCE.execute(this, includeRootPatternFirst(patterns));
   }

   /**
    * Get a single child node.<br/>
    * <br/>
    * If multiple children are found with same name it is considered a IllegalArgumentException.
    *
    * @param name The child node name
    * @return The named child node or null if non found
    * @throws IllegalArgumentException if multiple children with name exists.
    */
   public Node getSingle(String name)
   {
      return getSingle(Patterns.from(name));
   }

   Node getSingle(final Pattern... patterns)
   {
      return AbsoluteGetSingleQuery.INSTANCE.execute(this, includeRootPatternFirst(patterns));
   }

   /**
    * Get all children with a specific name.
    *
    * @param name The child node name.
    * @return All found children, or empty list if none found.
    */
   public List<Node> get(String name)
   {
      return get(Patterns.from(name));
   }

   /**
    * Get all children matching the specified query.
    *
    * @param query The query to use for finding relevant child nodes
    * @return All found children, or empty list if none found.
    */
   List<Node> get(final Pattern... patterns)
   {
      return AbsoluteGetQuery.INSTANCE.execute(this, includeRootPatternFirst(patterns));
   }

   /**
    * Remove all child nodes found at the given query.
    *
    * @return the {@link List} of removed children.
    * @throws IllegalArgumentException If the specified name is not specified
    */
   public List<Node> removeChildren(final String name) throws IllegalArgumentException
   {
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("Path must not be null or empty");
      }

      List<Node> found = get(name);
      for (Node child : found)
      {
         children.remove(child);
      }
      return found;
   }

   /**
    * Remove all child nodes found at the given {@link Pattern}s.
    *
    * @return the {@link List} of removed children.
    * @throws IllegalArgumentException If pattern is not specified
    */
   List<Node> removeChildren(final Pattern pattern, final Pattern... patterns)
   {
      // Precondition check
      final Pattern[] merged = this.validateAndMergePatternInput(pattern, patterns);

      final List<Node> found = get(merged);
      if (found == null)
      {
         return Collections.emptyList();
      }
      for (Node child : found)
      {
         children.remove(child);
      }
      return found;
   }

   /**
    * Remove a single child from this {@link Node}
    *
    * @return true if this node contained the given child
    */
   public boolean removeChild(final Node child)
   {
      return children.remove(child);
   }

   /**
    * Remove a single child from this {@link Node}
    *
    * @return true if this node contained the given child
    * @throws IllegalArgumentException if multiple children with name exist.
    */
   public Node removeChild(final String name)
   {
      final Node node = getSingle(name);
      if (node != null)
      {
         removeChild(node);
      }
      return node;
   }

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
   public Node text(Object text)
   {
      return text(String.valueOf(text));
   }

   /**
    * Set the Nodes text body.
    *
    * @param text The text content
    * @return This
    */
   public Node text(String text)
   {
      this.text = text;
      return this;
   }

   /**
    * Get the Nodes text body.
    *
    * @return Set body or null if none.
    */
   public String getText()
   {
      return text;
   }

   /**
    * Get the text value of the element found at the given query name. If no element is found, or no text exists, return
    * null;
    */
   public String getTextValueForPatternName(final String name)
   {
      Node n = this.getSingle(name);
      String text = n == null ? null : n.getText();
      return text;
   }

   /**
    * Get the text values of all elements found at the given query name. If no elements are found, or no text exists,
    * return an empty list;
    */
   public List<String> getTextValuesForPatternName(final String name)
   {
      List<String> result = new ArrayList<String>();
      List<Node> jars = this.get(name);
      for (Node node : jars)
      {
         String text = node.getText();
         if (text != null)
         {
            result.add(text);
         }
      }
      return Collections.unmodifiableList(result);
   }

   /**
    * Get the Nodes name.
    *
    * @return Given name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Get the Nodes parent.
    *
    * @return The given parent or null if root node.
    */
   public Node getParent()
   {
      return parent;
   }

   /**
    * Get all the defined children for this node in an immutable view.
    *
    * @return All children or empty list if none.
    */
   public List<Node> getChildren()
   {
      return Collections.unmodifiableList(children);
   }

   // -------------------------------------------------------------------------------------||
   // Override ----------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    *
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.getClass().getSimpleName() + "[" + name + "] " + "children["
               + (children != null ? children.size() : 0) + "] "
               + (attributes != null ? "attributes[" + attributes + "] " : "")
               + (text != null ? "text[" + text + "] " : "");
   }

   /**
    * Returns a multiline {@link String} format of this {@link Node} and all children
    *
    * @param verbose
    * @return
    */
   public String toString(final boolean verbose)
   {
      if (!verbose)
      {
         return this.toString();
      }

      final StringBuilder sb = new StringBuilder();
      sb.append("Listing of ");
      sb.append(Node.class.getSimpleName());
      sb.append(" starting at: ");
      sb.append(this.getName());
      sb.append('\n');
      this.appendNodeInfo(sb, 0, this);

      return sb.toString();
   }

   private void appendNodeInfo(final StringBuilder builder, final int level, final Node node)
   {
      final StringBuilder indent = new StringBuilder();
      for (int i = 0; i < level; i++)
      {
         indent.append('-');
      }

      builder.append(indent);
      builder.append('+');
      builder.append(SPACE);
      builder.append(node.getName());
      builder.append('(');
      builder.append(node.attributes);
      builder.append(')');
      final String nodeText = node.getText();
      if (nodeText != null)
      {
         builder.append(SPACE);
         builder.append(node.getText());
      }
      builder.append('\n');

      for (final Node child : node.children)
      {
         this.appendNodeInfo(builder, level + 1, child);
      }

   }

   /**
    * Validates that at least one pattern was specified, merges all patterns together, and returns the result
    *
    * @param pattern
    * @param patterns
    * @return
    */
   private Pattern[] validateAndMergePatternInput(final Pattern pattern, final Pattern... patterns)
   {
      // Precondition check
      if (pattern == null)
      {
         throw new IllegalArgumentException("At least one pattern must not be specified");
      }
      final List<Pattern> merged = new ArrayList<Pattern>();
      merged.add(pattern);
      for (final Pattern p : patterns)
      {
         merged.add(p);
      }
      return merged.toArray(PATTERN_CAST);
   }

   private Pattern[] includeRootPatternFirst(final Pattern... patterns)
   {
      return validateAndMergePatternInput(new Pattern(name), patterns);
   }
}
