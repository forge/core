/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.util.List;

import org.jboss.forge.shell.console.jline.console.completer.Completer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CompleterAdaptor implements Completer
{
   private final CommandCompleter wrapped;

   public CompleterAdaptor(final CommandCompleter wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public int complete(final String buffer, final int cursor, final List<CharSequence> candidates)
   {
      CommandCompleterState state = new BaseCommandCompleterState(buffer, null, cursor);

      if (wrapped != null)
      {
         wrapped.complete(state);
         candidates.addAll(state.getCandidates());
      }

      return state.getIndex();
   }
}
