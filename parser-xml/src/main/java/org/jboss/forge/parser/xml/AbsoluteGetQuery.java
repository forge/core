/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Form of {@link GetQuery} for retrieving nodes matching absolute patterns like '/root/node1/node2'. Sequence of
 * patterns must much a path from the root, where i-th pattern matches i-th element on the path from the root.
 * 
 * If no matches are found, <code>null</code> is returned.
 * 
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
enum AbsoluteGetQuery implements Query<List<Node>>
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

      // Delegate to recursive handler, starting at the top
      return findMatch(node, Arrays.asList(patterns));
   }

   protected List<Node> findMatch(Node start, List<Pattern> patterns)
   {
      // Get the next pattern in sequence
      final Pattern pattern = patterns.get(0);

      if (!pattern.matches(start))
      {
         return Collections.emptyList();
      }

      // Hold the matched Nodes
      final List<Node> matchedNodes = new ArrayList<Node>();

      if (patterns.size() == 1)
      {
         matchedNodes.add(start);
         return matchedNodes;
      }

      for (final Node child : start.getChildren())
      {
         // Only use patterns that haven't already matched
         final List<Pattern> remainingPatterns = patterns.subList(1, patterns.size());

         // Recursion point
         matchedNodes.addAll(findMatch(child, remainingPatterns));
      }

      return matchedNodes;
   }

}
