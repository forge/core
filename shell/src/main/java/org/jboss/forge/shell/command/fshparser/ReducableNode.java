/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.fshparser;

/**
 * @author Mike Brock .
 */
public class ReducableNode extends LogicalStatement
{
   private boolean nocommand = false;

   public ReducableNode(Node nest, boolean nocommand)
   {
      super(nest);

      /**
       * If the next node is just a value suppression operator, we discard it.
       */
      if ((this.nocommand = nocommand) || FSHParser.tokenMatch(nest, "@"))
      {
         super.nest = nest.next;
      }
   }

   public boolean isNocommand()
   {
      return nocommand;
   }
}
