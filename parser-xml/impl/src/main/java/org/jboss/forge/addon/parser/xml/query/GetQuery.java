/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.parser.xml.Node;
import org.jboss.forge.addon.parser.xml.NodeImpl;
import org.jboss.forge.addon.parser.xml.Pattern;
import org.jboss.forge.addon.parser.xml.util.QueryUtil;

/**
 * Obtains the {@link List} of {@link NodeImpl}s designated by the specified {@link PatternImpl}s under the specified root
 * {@link NodeImpl}.
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
    * @see org.jboss.shrinkwrap.descriptor.spi.node.query.Query#execute(org.jboss.NodeImpl.descriptor.spi.node.Node,
    *      org.jboss.forge.addon.parser.xml.PatternImpl.descriptor.spi.node.query.Pattern[])
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
    * Returns all {@link NodeImpl}s decendent from the specified start which match the specified {@link PatternImpl}s
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
