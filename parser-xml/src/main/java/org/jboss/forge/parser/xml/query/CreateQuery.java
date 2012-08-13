/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml.query;

import java.util.Map;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.util.QueryUtil;

/**
 * Creates the specified {@link Pattern}s starting at the specified {@link Node}.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public enum CreateQuery implements Query<Node>
{
   INSTANCE;

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.descriptor.spi.node.query.Query#execute(org.jboss.shrinkwrap.descriptor.spi.node.Node,
    *      org.jboss.shrinkwrap.descriptor.spi.node.query.Pattern[])
    */
   @Override
   public Node execute(final Node node, final Pattern... patterns)
   {
      // Precondition checks
      QueryUtil.validateNodeAndPatterns(node, patterns);

      Node returnValue = node;

      for (final Pattern pattern : patterns)
      {
         returnValue = new Node(pattern.getName(), returnValue).text(pattern.getText());
         for (Map.Entry<String, String> entry : pattern.getAttributes().entrySet())
         {
            returnValue.attribute(entry.getKey(), entry.getValue());
         }
      }
      return returnValue;
   }
}
