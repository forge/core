/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.Map;

/**
 * Creates the specified {@link Pattern}s starting at the specified {@link Node}.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
enum CreateQuery implements Query<Node>
{

   INSTANCE;

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.descriptor.spi.node.Query#execute(org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.shrinkwrap.descriptor.spi.node.Node,
    *      org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.query.shrinkwrap.descriptor.spi.node.Pattern[])
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
