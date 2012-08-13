/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.completer;

import java.util.List;
import java.util.Queue;

public interface CommandCompleterState
{
   public int getOriginalIndex();

   public Queue<String> getOriginalTokens();

   public int getIndex();

   public String getBuffer();

   public String getLastBuffer();

   public boolean isFinalTokenComplete();

   public boolean hasSuggestions();

   public void setIndex(int newIndex);

   public Queue<String> getTokens();

   public List<String> getCandidates();
}
