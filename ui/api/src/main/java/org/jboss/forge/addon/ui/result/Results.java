/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;

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
    * Create a {@link NavigationResult} using the given {@link UICommand} type as the target.
    */
   public static final NavigationResult navigateTo(Class<? extends UICommand> next)
   {
      if (next == null)
      {
         return null;
      }
      NavigationResultBuilder builder = navigationBuilder();
      builder.add(next);
      return builder.build();
   }

   /**
    * Create a {@link NavigationResult} using the given {@link UICommand} array as the target.
    */
   public static final NavigationResult navigateTo(Class<? extends UICommand>[] next)
   {
      if (next == null)
      {
         return null;
      }
      NavigationResultBuilder builder = navigationBuilder();
      for (Class<? extends UICommand> type : next)
      {
         builder.add(type);
      }
      return builder.build();
   }

   /**
    * Create a {@link NavigationResult} using the given {@link UICommand} types as the targets.
    */
   @SuppressWarnings("unchecked")
   public static final NavigationResult navigateTo(Class<? extends UICommand> next,
            Class<? extends UICommand>... additional)
   {
      if (next == null)
         return null;

      NavigationResultBuilder builder = navigationBuilder();
      builder.add(next);
      for (Class<? extends UICommand> type : additional)
      {
         builder.add(type);
      }
      return builder.build();
   }

   public static NavigationResultBuilder navigationBuilder()
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      return builder;
   }

   /**
    * Aggregates a list of results into one single {@link CompositeResult}
    */
   public static CompositeResult aggregate(Iterable<Result> results)
   {
      List<Result> resultList = toList(results);
      return CompositeResultImpl.from(resultList);
   }

   // TODO: Move to an utils method
   private static <T> List<T> toList(Iterable<T> iterable)
   {
      if (iterable == null)
      {
         return null;
      }
      else if (iterable instanceof List)
      {
         return (List<T>) iterable;
      }
      else
      {
         List<T> list = new ArrayList<>();
         for (T obj : iterable)
         {
            list.add(obj);
         }
         return list;
      }
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