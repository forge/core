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
public class TokenNode extends Node
{
   protected String value;

   public TokenNode()
   {
   }

   public TokenNode(String value)
   {
      if (value.startsWith("$"))
      {
         this.value = value.substring(1);
      }
      else
      {
         this.value = value;
      }
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   @Override
   public String toString()
   {
      return value + (next != null ? " " + next.toString() : "");
   }
}
