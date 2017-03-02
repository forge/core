/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result;

import java.util.List;
import java.util.Optional;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.furnace.util.Lists;

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
    * Create a successful {@link Result} with a message and an optional entity.
    */
   public static final Result success(String message, Object entity)
   {
      return new SuccessfulResult(message, entity);
   }

   /**
    * Create a failed {@link Result}.
    */
   public static final Failed fail()
   {
      return fail(null);
   }

   /**
    * Create a failed {@link Result} with a message.
    */
   public static final Failed fail(String message)
   {
      return new FailedResult(message);
   }

   /**
    * Create a failed {@link Result} with a message and {@link Throwable} root cause.
    */
   public static final Failed fail(String message, Throwable e)
   {
      return new FailedResult(message, e);
   }

   /**
    * Create a failed {@link Result} with a message and {@link Throwable} root cause.
    */
   public static final Failed fail(String message, Throwable e, Object entity)
   {
      return new FailedResult(message, e, entity);
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
      List<Result> resultList = Lists.toList(results);
      return CompositeResultImpl.from(resultList);
   }

   public static CompositeResult aggregate(Iterable<Result> results, Object entity)
   {
      List<Result> resultList = Lists.toList(results);
      return CompositeResultImpl.from(resultList, entity);
   }

   private static class SuccessfulResult implements Result
   {
      private final String message;
      private final Object entity;

      SuccessfulResult(String message)
      {
         this.message = message;
         this.entity = null;
      }

      SuccessfulResult(String message, Object entity)
      {
         this.message = message;
         this.entity = entity;
      }

      @Override
      public String getMessage()
      {
         return message;
      }

      @Override
      public Optional<Object> getEntity()
      {
         return Optional.ofNullable(entity);
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
      private final Object entity;
      private final Throwable e;

      FailedResult(String message)
      {
         this.message = message;
         this.e = null;
         this.entity = null;
      }

      FailedResult(String message, Throwable e)
      {
         this.message = message;
         this.e = e;
         this.entity = null;
      }

      FailedResult(String message, Throwable e, Object entity)
      {
         this.message = message;
         this.e = e;
         this.entity = entity;
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
      public Optional<Object> getEntity()
      {
         return Optional.ofNullable(entity);
      }

      @Override
      public String toString()
      {
         return "Failed [" + message + "]";
      }
   }
}