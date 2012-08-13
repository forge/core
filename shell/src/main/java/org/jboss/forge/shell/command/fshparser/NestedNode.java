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
public class NestedNode extends Node
{
   protected Node nest;

   public NestedNode(Node nest)
   {
      this.nest = nest;
   }

   public Node getNest()
   {
      return nest;
   }

   public void setNest(Node nest)
   {
      this.nest = nest;
   }

   @Override
   public String toString()
   {
      return "{" + nest + "}" + (next != null ? " " + next.toString() : "");
   }
}
