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
