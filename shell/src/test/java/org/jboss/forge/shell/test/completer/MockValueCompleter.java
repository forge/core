/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.completer;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.shell.completer.CommandCompleter;
import org.jboss.forge.shell.completer.CommandCompleterState;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockValueCompleter implements CommandCompleter
{
   private final List<String> values = Arrays.asList("Foo", "Bar", "Baz", "Cal", "Cav");

   @Override
   public void complete(final CommandCompleterState state)
   {
      String peek = state.getTokens().peek();

      if (peek != null)
      {
         for (String val : values)
         {
            if (val.startsWith(peek))
            {
               state.getCandidates().add(val);
               state.setIndex(state.getOriginalIndex() - peek.length());
            }
         }
      }
      else
      {
         state.getCandidates().addAll(values);
      }
   }

}
