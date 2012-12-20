/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;


public abstract class Result
{

   private final Class<? extends UICommand> command;
   private final String message;

   public static final Result success()
   {
      return new ResultSuccess((String) null);
   }

   public static final Result success(String message)
   {
      return new ResultSuccess(message);
   }

   public static final Result success(Class<? extends UICommand> command)
   {
      return new ResultSuccess(command);
   }

   public static final Result success(Class<? extends UICommand> next, String message)
   {
      return new ResultSuccess(message);
   }

   public static final Result fail(String message)
   {
      return new ResultFail(message);
   }

   public static final Result fail(Class<? extends UICommand> command)
   {
      return new ResultFail(command);
   }

   public static final Result fail(Class<? extends UICommand> next, String message)
   {
      return new ResultFail(message);
   }

   Result(String message)
   {
      this.message = message;
      this.command = null;
   }

   Result(Class<? extends UICommand> command)
   {
      this.message = null;
      this.command = command;
   }

   Result(Class<? extends UICommand> command, String message)
   {
      this.command = command;
      this.message = message;
   }

   public String getMessage()
   {
      return this.message;
   }

   public Class<? extends UICommand> getCommand()
   {
      return this.command;
   }
}