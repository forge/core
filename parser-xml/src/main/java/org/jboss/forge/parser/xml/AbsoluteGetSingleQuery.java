/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.List;

/**
 * Form of {@link GetQuery} used as a convenience to retrieve a single result. If more than one match is found,
 * {@link IllegalArgumentException} will be thrown. If no matches are found, <code>null</code> is returned.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
enum AbsoluteGetSingleQuery implements Query<Node>
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

      final List<Node> nodes = AbsoluteGetQuery.INSTANCE.execute(node, patterns);

      if (nodes == null || nodes.isEmpty())
      {
         return null;
      }
      if (nodes.size() > 1)
      {
         throw new IllegalArgumentException("Multiple nodes matching expression found");
      }
      return nodes.get(0);
   }

}
