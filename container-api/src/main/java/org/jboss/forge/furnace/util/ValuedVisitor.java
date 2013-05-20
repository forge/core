/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <INPUTTYPE> the instance type.
 * @param <VALUETYPE> the result value type.
 */
public abstract class ValuedVisitor<VALUETYPE, INPUTTYPE> implements Visitor<INPUTTYPE>
{
   private VALUETYPE result;

   /**
    * Get the result value.
    */
   public VALUETYPE getResult()
   {
      return result;
   }

   /**
    * Set the result value.
    */
   protected void setResult(VALUETYPE result)
   {
      this.result = result;
   }

   /**
    * Return <code>true</code> if the {@link #getResult()} is not <code>null</code>.
    */
   public boolean hasResult()
   {
      return getResult() != null;
   }
}
