/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.completer;

public class NullCommandCompleter implements CommandCompleter
{

   @Override
   public void complete(CommandCompleterState state)
   {
      throw new UnsupportedOperationException("The " + getClass().getSimpleName()
               + " completer should be replaced with an actual CommandCompleter");
   }

}
