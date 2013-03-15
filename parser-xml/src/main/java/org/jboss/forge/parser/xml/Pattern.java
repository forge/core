/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A pattern that may be executed as part of a {@link Query} upon a {@link Node} in a search, or used to define a target
 * {@link Node} to be created. Value object analogous to XPath patterns; it describes something in the tree structure of
 * {@link Node}.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
final class Pattern
{
   // -------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   private final Map<String, String> attributes = new HashMap<String, String>();

   private final String name;

   private String text;

   // -------------------------------------------------------------------------------------||
   // Constructors -----------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link Pattern} instance with the specified name
    * 
    * @param name
    * @throws IllegalArgumentException If the name is not specified
    */
   public Pattern(final String name) throws IllegalArgumentException
   {
      // Precondition check
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }

      // Set
      this.name = name;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.getClass().getSimpleName() + " [attributes=" + attributes + ", name=" + name + ", text=" + text
               + "]";
   }

   /**
    * The node name
    * 
    * @return
    */
   public String getName()
   {
      return name;
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
    * @return This Node
    * @see #attribute(String, String)
    */
   public Pattern attribute(final String name, final Object value)
   {
      return attribute(name, String.valueOf(value));
   }

   /**
    * Add or override a named attribute.<br/>
    * 
    * @param name The attribute name
    * @param value The given value
    * @return This Node
    */
   public Pattern attribute(final String name, final String value)
   {
      attributes.put(name, value);
      return this;
   }

   /**
    * Returns an immutable view of all defined attributes for this Node.
    * 
    * @return All defined attributes.
    */
   public Map<String, String> getAttributes()
   {
      return Collections.unmodifiableMap(attributes);
   }

   /**
    * Obtains the value of the named attribute, or null if not present
    * 
    * @param name
    * @return
    * @throws IllegalArgumentException If name is not specified
    */
   public String getAttribute(final String name) throws IllegalArgumentException
   {
      return attributes.get(name);
   }

   /**
    * Returns true if and only if the specified {@link Node} values match the data contained in this {@link Pattern}
    * value object
    * 
    * @param node
    * @return
    * @throws IllegalArgumentException If the {@link Node} is not specified
    */
   public boolean matches(final Node node) throws IllegalArgumentException
   {
      // Precondition checks
      if (node == null)
      {
         throw new IllegalArgumentException("node must be specified");
      }

      if (!name.equals(node.getName()))
      {
         return false;
      }

      if ((text != null && node.getText() == null) || (text != null && !text.trim().equals(node.getText().trim())))
      {
         return false;
      }

      if (attributes != null)
      {
         for (final Map.Entry<String, String> attribute : attributes.entrySet())
         {
            final String attrValue = attribute.getValue();
            final String attrName = attribute.getKey();

            if (!attrValue.equals(node.getAttribute(attrName)))
            {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Sets the node text to match, returning a reference to this {@link Pattern}
    * 
    * @param text The text value which should be matched
    * @return This {@link Pattern}
    */
   public Pattern text(final String text)
   {
      this.text = text;
      return this;
   }

   /**
    * Gets the node text to be matched
    * 
    * @return
    */
   public String getText()
   {
      return text;
   }
}
