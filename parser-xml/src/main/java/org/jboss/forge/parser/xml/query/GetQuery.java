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
package org.jboss.forge.parser.xml.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.util.QueryUtil;

/**
 * Obtains the {@link List} of {@link Node}s designated by the specified {@link Pattern}s under the specified root
 * {@link Node}.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public enum GetQuery implements Query<List<Node>>
{

   /**
    * Instance
    */
   INSTANCE;

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.descriptor.spi.node.query.Query#execute(org.jboss.shrinkwrap.descriptor.spi.node.Node,
    *      org.jboss.shrinkwrap.descriptor.spi.node.query.Pattern[])
    */
   @Override
   public List<Node> execute(final Node node, final Pattern... patterns)
   {
      // Precondition checks
      QueryUtil.validateNodeAndPatterns(node, patterns);

      // Represent as a list
      final List<Pattern> patternList = Arrays.asList(patterns);

      // Delegate to recursive handler, starting at the top
      return findMatch(node, patternList);
   }

   /**
    * Returns all {@link Node}s decendent from the specified start which match the specified {@link Pattern}s
    * 
    * @param start
    * @param patterns
    * @return
    */
   private List<Node> findMatch(final Node start, final List<Pattern> patterns)
   {
      // Hold the matched Nodes
      final List<Node> matchedNodes = new ArrayList<Node>();

      // Get the next pattern in sequence
      final Pattern pattern = patterns.get(0);

      // Check that there's a pattern to match
      if (pattern == null)
      {
         return matchedNodes;
      }

      // Init a flag
      boolean foundMatch = false;

      // See if we've got a match
      if (pattern.matches(start))
      {
         // Set flag
         foundMatch = true;

         // If no more patterns to check, we're at the end of the line; just add this Node
         if (patterns.size() == 1)
         {
            matchedNodes.add(start);
            return matchedNodes;
         }
      }

      // Check all children
      for (final Node child : start.getChildren())
      {
         // Only use patterns that haven't already matched
         final List<Pattern> sub = patterns.subList(foundMatch ? 1 : 0, patterns.size());

         // Recursion point
         matchedNodes.addAll(findMatch(child, sub));
      }

      // Return
      return matchedNodes;
   }

}
