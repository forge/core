/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.fshparser;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Mike Brock .
 */
public class LogicalStatement extends NestedNode
{
   public LogicalStatement(final Node nest)
   {
      super(nest);
   }

   public Queue<String> getTokens(final FSHRuntime runtime)
   {
      Queue<String> newQueue = new LinkedList<String>();
      Node n = nest;
      do
      {
         if (n instanceof TokenNode)
         {
            newQueue.add(((TokenNode) n).getValue());
         }
         else if (n instanceof LogicalStatement)
         {
            newQueue.add("(");
            newQueue.addAll(((LogicalStatement) n).getTokens(runtime));
            newQueue.add(")");
         }
         else
         {
            throw new RuntimeException("uh-oh");
         }
      }
      while ((n = n.next) != null);

      return newQueue;
   }

}
