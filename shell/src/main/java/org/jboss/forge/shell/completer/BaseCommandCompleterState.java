/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.jboss.forge.shell.command.parser.Tokenizer;

/**
 * Holds state during TAB completion.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BaseCommandCompleterState implements CommandCompleterState
{
   /*
    * Immutable state
    */
   private final String buffer;
   private final String lastBuffer;
   private final int originalIndex;
   private final boolean tokenComplete;

   /*
    * Mutable State
    */
   private final Queue<String> tokens;
   private final List<String> candidates = new ArrayList<String>();
   private int index;

   public BaseCommandCompleterState(final String initialBuffer, final String lastBuffer, final int initialIndex)
   {
      this.buffer = initialBuffer;
      this.lastBuffer = lastBuffer;
      this.index = initialIndex;
      this.originalIndex = initialIndex;
      this.tokens = new Tokenizer().tokenize(initialBuffer);
      this.tokenComplete = buffer.matches("^.*\\s+$");
   }

   /*
    * Immutable State
    */
   @Override
   public int getOriginalIndex()
   {
      return originalIndex;
   }

   @Override
   public Queue<String> getOriginalTokens()
   {
      return new Tokenizer().tokenize(buffer);
   }

   @Override
   public String getBuffer()
   {
      return buffer;
   }

   @Override
   public String getLastBuffer()
   {
      return lastBuffer;
   }

   @Override
   public boolean isFinalTokenComplete()
   {
      return tokenComplete;
   }

   /*
    * Inquisitors
    */
   @Override
   public boolean hasSuggestions()
   {
      return !candidates.isEmpty();
   }

   /*
    * Modifiers
    */
   /**
    * Set the position where completion candidates should begin.
    */
   @Override
   public void setIndex(final int newIndex)
   {
      this.index = newIndex;
   }

   @Override
   public int getIndex()
   {
      return index;
   }

   @Override
   public Queue<String> getTokens()
   {
      return tokens;
   }

   @Override
   public List<String> getCandidates()
   {
      return candidates;
   }

   public boolean isDuplicateBuffer()
   {
      return (buffer != null) && buffer.equals(lastBuffer);
   }
}
