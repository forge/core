/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.completer;

/**
 * The simplest possible command option completer. Matching of tokens is handled automatically based on comparing the
 * partial token against the beginning of each candidate, but also provides the least control over how completion
 * occurs.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class SimpleTokenCompleter implements CommandCompleter
{
   /**
    * Return a list of tokens to be considered as possible completions for the current input buffer. Typically, this
    * should be a list of all possible candidates, but might vary depending on current state. If returning Objects,
    * ensure that their {{@link #toString()} method is appropriately overloaded; the value returned will be used in
    * completion.
    */
   public abstract Iterable<?> getCompletionTokens();

   @Override
   public void complete(final CommandCompleterState state)
   {
      Iterable<?> values;
      try
      {
         values = getCompletionTokens();
         String peek = state.getTokens().peek();

         if ((state.getTokens().size() <= 1) && values != null)
         {
            for (Object val : values)
            {
               if (val != null)
               {
                  String prop = val.toString();
                  if (prop.startsWith(peek == null ? "" : peek))
                  {
                     state.getCandidates().add(prop + " ");
                     state.setIndex(state.getOriginalIndex() - (peek == null ? 0 : peek.length()));
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         // TODO could not get options. this should eventually be logged
      }
   }

}
