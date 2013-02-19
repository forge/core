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
   private final Class<? extends UICommand> command;
   private final String message;

   public static final Results success()
   {
      String msg = null;
      return new ResultSuccess(msg);
   }

   public static final Results success(String message)
   {
      return new ResultSuccess(message);
   }

   public static final Results success(Class<? extends UICommand> command)
   {
      return new ResultSuccess(command);
   }

   public static final Results success(Class<? extends UICommand> next, String message)
   {
      return new ResultSuccess(next, message);
   }

   public static final Results fail(String message)
   {
      return new ResultFail(message);
   }

   public static final Results fail(Class<? extends UICommand> command)
   {
      return new ResultFail(command);
   }

   public static final Results fail(Class<? extends UICommand> next, String message)
   {
      return new ResultFail(next, message);
   }

   Results(String message)
   {
      this.message = message;
      this.command = null;
   }

   Results(Class<? extends UICommand> command)
   {
      this.message = null;
      this.command = command;
   }

   Results(Class<? extends UICommand> command, String message)
   {
      this.command = command;
      this.message = message;
   }

   @Override
   public String getMessage()
   {
      return this.message;
   }

   @Override
   public Class<? extends UICommand> getCommand()
   {
      return this.command;
   }
}