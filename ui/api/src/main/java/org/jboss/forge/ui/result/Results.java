/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.result;

import org.jboss.forge.ui.UICommand;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Results implements Result
{
   private final String message;

   public static final Result success()
   {
      return success(null);
   }

   public static final Result success(String message)
   {
      return new ResultSuccess(message);
   }

   public static final Result fail(String message)
   {
      return new ResultFail(message);
   }

   public static final NavigationResult navigateTo(Class<? extends UICommand> next)
   {
      return navigateTo(next, null);
   }

   public static final NavigationResult navigateTo(Class<? extends UICommand> next, String message)
   {
      return new NavigationResultImpl(message, next);
   }

   Results(String message)
   {
      this.message = message;
   }

   @Override
   public String getMessage()
   {
      return this.message;
   }
}