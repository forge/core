/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import org.jboss.forge.addon.ui.UICommand;

/**
 * Utilities for creating {@link Result} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Results
{
   public static final Result success()
   {
      return success(null);
   }

   public static final Result success(String message)
   {
      return new SuccessfulResult(message);
   }

   public static final Result fail(String message)
   {
      return new FailedResult(message);
   }

   public static final Result fail(String message, Throwable e)
   {
      return new FailedResult(message, e);
   }

   @SuppressWarnings("unchecked")
   public static final NavigationResult navigateTo(Class<? extends UICommand> next)
   {
      if (next == null)
      {
         return null;
      }
      return navigateTo(null, next, new Class[0]);
   }

   public static final NavigationResult navigateTo(Class<? extends UICommand> next,
            Class<? extends UICommand>... additional)
   {
      return navigateTo(null, next, additional);
   }

   @SuppressWarnings("unchecked")
   public static final NavigationResult navigateTo(String message, Class<? extends UICommand> next,
            Class<? extends UICommand>... additional)
   {
      if (next == null)
         return null;

      Class<? extends UICommand>[] all = new Class[1 + additional.length];
      all[0] = next;
      System.arraycopy(additional, 0, all, 1, additional.length);
      return new NavigationResultImpl(message, all);
   }

   private static class SuccessfulResult implements Result
   {
      private String message;

      SuccessfulResult(String message)
      {
         this.message = message;
      }

      @Override
      public String getMessage()
      {
         return message;
      }
   }

   private static class FailedResult implements Result, Failed
   {
      private String message;
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
   }
}