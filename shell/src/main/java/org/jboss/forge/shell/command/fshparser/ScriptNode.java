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
public class ScriptNode extends LogicalStatement
{
   private boolean nocommand = false;

   public ScriptNode(Node nest, boolean nocommand)
   {
      super(nest);
      this.nocommand = nocommand;
   }

   public boolean isNocommand()
   {
      return nocommand;
   }
}
