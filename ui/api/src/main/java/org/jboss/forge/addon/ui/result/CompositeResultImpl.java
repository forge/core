/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result;

import java.util.List;
import java.util.Optional;

import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Strings;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
abstract class CompositeResultImpl implements CompositeResult
{
   private final List<Result> results;
   private final Object entity;

   CompositeResultImpl(List<Result> results, Object entity)
   {
      Assert.notNull(results, "Result list cannot be null");
      this.results = results;
      this.entity = entity;
   }

   @Override
   public List<Result> getResults()
   {
      return results;
   }

   @Override
   public Optional<Object> getEntity()
   {
      return Optional.ofNullable(entity);
   }

   @Override
   public String getMessage()
   {
      StringBuilder sb = new StringBuilder();
      for (Result result : results)
      {
         if (!Strings.isNullOrEmpty(result.getMessage()))
         {
            if (sb.length() > 0)
            {
               sb.append(System.lineSeparator());
            }
            sb.append(result instanceof Failed ? "***ERROR*** " : "***SUCCESS*** ").append(result.getMessage());
         }
      }
      return sb.toString();
   }

   public static CompositeResult from(List<Result> results, Object entity)
   {
      boolean failed = false;
      Throwable throwable = null;
      for (Result result : results)
      {
         if (result instanceof Failed)
         {
            failed = true;
            throwable = ((Failed) result).getException();
            break;
         }
      }

      if (failed)
         return new CompositeResultFailed(results, throwable, entity);
      return new CompositeResultSuccess(results, entity);
   }

   public static CompositeResult from(List<Result> results)
   {
      return from(results, null);
   }

   private static class CompositeResultFailed extends CompositeResultImpl implements Result, Failed
   {
      private final Throwable exception;

      public CompositeResultFailed(List<Result> results, Throwable e, Object entity)
      {
         super(results, entity);
         this.exception = e;
      }

      @Override
      public Throwable getException()
      {
         return exception;
      }
   }

   private static class CompositeResultSuccess extends CompositeResultImpl implements Result
   {
      public CompositeResultSuccess(List<Result> results, Object entity)
      {
         super(results, entity);
      }
   }
}
