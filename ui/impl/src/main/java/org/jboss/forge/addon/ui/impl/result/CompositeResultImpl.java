/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.result;

import java.util.List;

import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CompositeResultImpl implements CompositeResult
{
   public final List<Result> results;

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

   public String getMessage()
   {
      throw new UnsupportedOperationException(
               "getMessage() should not be called in a CompositeResult. Call getResults() instead.");
   }

}
