/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.parser.xml.util;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.query.Pattern;
import org.jboss.forge.parser.xml.query.Query;

/**
 * Helper util for building {@link Query} implementations
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public final class QueryUtil
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
      if ((patterns == null) || (patterns.length == 0))
      {
         throw new IllegalArgumentException("At least one pattern must be specified");
      }

      // OK
   }
}
