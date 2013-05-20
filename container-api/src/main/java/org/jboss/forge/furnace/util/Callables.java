/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.furnace.util;

import java.util.concurrent.Callable;

/**
 * Utility to create and handle {@link Callable} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Callables
{
   private Callables()
   {
   }

   /**
    * Wrap a constant value into a {@link Callable} instance.
    */
   public static <T> Callable<T> returning(T value)
   {
      return new ConstantCallable<T>(value);
   }

   /**
    * Calls the {@link Callable} avoiding the checked {@link Exception}.
    */
   public static <T> T call(Callable<T> c)
   {
      if (c == null)
      {
         return null;
      }
      try
      {
         return c.call();
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error invoking Callable [c]", e);
      }
   }

   /**
    * Simple {@link Callable} class that returns the given value.
    */
   static class ConstantCallable<V> implements Callable<V>
   {
      private final V value;

      public ConstantCallable(V value)
      {
         this.value = value;
      }

      @Override
      public V call()
      {
         return value;
      }
   }

}
