/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Form of {@link GetQuery} for retrieving nodes matching relative patterns like '//node1/node2'.
 * 
 * If no matches are found, <code>null</code> is returned.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
enum RelativeGetQuery implements Query<List<Node>>
{

   INSTANCE;

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.descriptor.spi.node.Query#execute(org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.shrinkwrap.descriptor.spi.node.Node,
    *      org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.query.shrinkwrap.descriptor.spi.node.Pattern[])
    */
   @Override
   public List<Node> execute(final Node node, final Pattern... patterns) throws IllegalArgumentException
   {
      // Precondition checks
      QueryUtil.validateNodeAndPatterns(node, patterns);

      // Represent as a list
      List<Pattern> patternSequence = Arrays.asList(patterns);

      // Delegate to recursive handler, starting at the top
      return findMatch(node, patternSequence, patternSequence);
   }

   private List<Node> findMatch(final Node start, final List<Pattern> patternSequence,
            final List<Pattern> entirePatternSequence)
   {
      // Hold the matched Nodes
      final List<Node> matchedNodes = new ArrayList<Node>();

      // Get the next pattern in sequence
      final Pattern pattern = patternSequence.get(0);

      // See if we've got a match
      if (pattern.matches(start))
      {

         // If no more patterns to check, we're at the end of the line; just add this Node
         if (patternSequence.size() == 1)
         {
            matchedNodes.add(start);
         }
         else
         {
            for (final Node child : start.getChildren())
            {
               // Only use patterns that haven't already matched
               final List<Pattern> remainingPatterns = patternSequence.subList(1, patternSequence.size());

               // Recursion point
               matchedNodes.addAll(findMatch(child, remainingPatterns, entirePatternSequence));
            }
         }

      }

      // Apply whole pattern sequence starting from the subtrees
      // created by node's children
      for (final Node child : start.getChildren())
      {
         matchedNodes.addAll(findMatch(child, entirePatternSequence, entirePatternSequence));
      }

      return matchedNodes;
   }

}
