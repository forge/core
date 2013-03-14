/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.forge.container.addons.Addon;

/**
 * A pass-through {@link Future} that simply returns the specified value immediately.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CompletedFuture<T> implements Future<Addon>
{
   private Addon value;

   public CompletedFuture(Addon value)
   {
      super();
      this.value = value;
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning)
   {
      return false;
   }

   @Override
   public Addon get() throws InterruptedException, ExecutionException
   {
      return value;
   }

   @Override
   public Addon get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
   {
      return value;
   }

   @Override
   public boolean isCancelled()
   {
      return false;
   }

   @Override
   public boolean isDone()
   {
      return true;
   }

}
