/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git.gitignore;

import javax.inject.Inject;

import org.jboss.forge.shell.completer.SimpleTokenCompleter;
import org.jboss.forge.shell.plugins.Current;

/**
 * @author Dan Allen
 */
public class GitIgnorePatternCompleter extends SimpleTokenCompleter
{

   @Inject
   @Current
   private GitIgnoreResource resource;

   @Override
   public Iterable<?> getCompletionTokens()
   {
      return resource.getPatterns();
   }

}
