/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import java.util.List;

import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
abstract class CompositeResultImpl implements CompositeResult
{
   private final List<Result> results;

   public CompositeResultImpl(List<Result> results)
   {
      Assert.notNull(results, "Result list cannot be null");
      this.results = results;
   }

   @Override
   public List<Result> getResults()
   {
      return results;
   }

   @Override
   public String getMessage()
   {
      throw new UnsupportedOperationException(
               "getMessage() should not be called in a CompositeResult. Call getResults() instead.");
   }

   public static CompositeResult from(List<Result> results)
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
         return new CompositeResultFailed(results, throwable);
      return new CompositeResultSuccess(results);
   }

   private static class CompositeResultFailed extends CompositeResultImpl implements Result, Failed
   {
      private final Throwable exception;

      public CompositeResultFailed(List<Result> results, Throwable e)
      {
         super(results);
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
      public CompositeResultSuccess(List<Result> results)
      {
         super(results);
      }
   }
}
