/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin.project;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * Provides completion for project build properties
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RepositoryCompleter extends SimpleTokenCompleter
{
   @Inject
   private Project project;

   @Override
   public List<Object> getCompletionTokens()
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      List<DependencyRepository> repositories = deps.getRepositories();

      List<Object> result = new ArrayList<Object>();
      for (DependencyRepository dependencyRepository : repositories)
      {
         result.add(dependencyRepository.getUrl());
      }

      return result;
   }

}
