/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;


public final class ResultFail extends Result
{
   ResultFail(String message)
   {
      super(message);
   }

   ResultFail(Class<? extends UICommand> command)
   {
      super(command);
   }

   ResultFail(Class<? extends UICommand> command, String message)
   {
      super(command, message);
   }
}