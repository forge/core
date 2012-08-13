/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin.project;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.completer.CommandCompleter;
import org.jboss.forge.shell.completer.CommandCompleterState;

/**
 * Provides completion for project build properties
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyPropertyCompleter implements CommandCompleter
{
   @Inject
   private Project project;

   @Override
   public void complete(final CommandCompleterState state)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      Set<String> properties = deps.getProperties().keySet();
      String peek = state.getTokens().peek();

      if ((state.getTokens().size() <= 1))
      {
         for (String prop : properties)
         {
            if (prop.startsWith(peek == null ? "" : peek))
            {
               state.getCandidates().add(prop);
               state.setIndex(state.getOriginalIndex() - (peek == null ? 0 : peek.length()));
            }
         }
      }
   }

}
