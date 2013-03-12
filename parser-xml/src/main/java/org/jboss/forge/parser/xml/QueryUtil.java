/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

/**
 * Helper util for building {@link Query} implementations
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
final class QueryUtil
{

   // -------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * No instances allowed
    */
   private QueryUtil()
   {
      throw new UnsupportedOperationException("No instances");
   }

   // -------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Validates input
    * 
    * @param node
    * @param patterns
    * @throws IllegalArgumentException If the {@link Node} is not specified or no {@link Pattern}s are specified
    */
   public static void validateNodeAndPatterns(final Node node, final Pattern... patterns)
            throws IllegalArgumentException
   {
      // Precondition checks
      if (node == null)
      {
         throw new IllegalArgumentException("node must be specified");
      }
      if (patterns == null || patterns.length == 0)
      {
         throw new IllegalArgumentException("At least one pattern must be specified");
      }

      // OK
   }
}
