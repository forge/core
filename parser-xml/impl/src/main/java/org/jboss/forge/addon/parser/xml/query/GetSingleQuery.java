/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml.query;

import java.util.List;

import org.jboss.forge.addon.parser.xml.Node;
import org.jboss.forge.addon.parser.xml.Pattern;
import org.jboss.forge.addon.parser.xml.util.QueryUtil;

/**
 * Form of {@link GetQuery} used as a convenience to retrieve a single result. If more than one match is found,
 * {@link IllegalArgumentException} will be thrown. If no matches are found, <code>null</code> is returned.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public enum GetSingleQuery implements Query<Node>
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
   public Node execute(final Node node, final Pattern... patterns)
   {
      // Precondition checks
      QueryUtil.validateNodeAndPatterns(node, patterns);

      final List<Node> nodes = GetQuery.INSTANCE.execute(node, patterns);

      if ((nodes == null) || (nodes.size() == 0))
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
