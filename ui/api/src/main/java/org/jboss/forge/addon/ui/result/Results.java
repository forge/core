/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import org.jboss.forge.addon.ui.command.UICommand;

/**
 * Utilities for creating {@link Result} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Results
{
   /**
    * Create a successful {@link Result}.
    */
   public static final Result success()
   {
      return success(null);
   }

   /**
    * Create a successful {@link Result} with a message.
    */
   public static final Result success(String message)
   {
      return new SuccessfulResult(message);
   }

   /**
    * Create a failed {@link Result}.
    */
   public static final Result fail()
   {
      return fail(null);
   }

   /**
    * Create a failed {@link Result} with a message.
    */
   public static final Result fail(String message)
   {
      return new FailedResult(message);
   }

   /**
    * Create a failed {@link Result} with a message and {@link Throwable} root cause.
    */
   public static final Result fail(String message, Throwable e)
   {
      return new FailedResult(message, e);
   }

   /**
    * Create a failed {@link NavigationResult} using the given {@link UICommand} type as the target.
    */
   @SuppressWarnings("unchecked")
   public static final NavigationResult navigateTo(Class<? extends UICommand> next)
   {
      if (next == null)
      {
         return null;
      }
      return navigateTo(next, new Class[0]);
   }

   /**
    * Create a failed {@link NavigationResult} using the given {@link UICommand} array as the target.
    */
   public static final NavigationResult navigateTo(Class<? extends UICommand>[] next)
   {
      if (next == null)
      {
         return null;
      }
      return new NavigationResultImpl(next);
   }

   /**
    * Create a failed {@link NavigationResult} using the given {@link UICommand} types as the targets.
    */
   @SuppressWarnings("unchecked")
   public static final NavigationResult navigateTo(Class<? extends UICommand> next,
            Class<? extends UICommand>... additional)
   {
      if (next == null)
         return null;

      Class<? extends UICommand>[] all = new Class[1 + additional.length];
      all[0] = next;
      System.arraycopy(additional, 0, all, 1, additional.length);
      return new NavigationResultImpl(all);
   }

   private static class SuccessfulResult implements Result
   {
      private final String message;

      SuccessfulResult(String message)
      {
         this.message = message;
      }

      @Override
      public String getMessage()
      {
         return message;
      }

      @Override
      public String toString()
      {
         return "Success [" + message + "]";
      }
   }

   private static class FailedResult implements Result, Failed
   {
      private final String message;
      private Throwable e;

      FailedResult(String message)
      {
         this.message = message;
      }

      public FailedResult(String message, Throwable e)
      {
         this.message = message;
         this.e = e;
      }

      @Override
      public String getMessage()
      {
         return message;
      }

      @Override
      public Throwable getException()
      {
         return e;
      }

      @Override
      public String toString()
      {
         return "Failed [" + message + "]";
      }
   }
}