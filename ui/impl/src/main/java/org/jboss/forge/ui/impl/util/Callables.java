/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.util;

import java.util.concurrent.Callable;

/**
 * Utilities to handle {@link Callable} objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */

public final class Callables
{
   private Callables()
   {
   }

   /**
    * Wrap a constant value into a Callable Object
    *
    * @param value
    * @return
    */
   public static <T> Callable<T> returning(T value)
   {
      return new ConstantCallable<T>(value);
   }

   /**
    * Calls the {@link Callable} avoiding the checked exception
    *
    * @param c
    * @return
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
      catch (RuntimeException re)
      {
         throw re;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Simple callable class that returns the same value
    *
    * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
    *
    * @param <V>
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
