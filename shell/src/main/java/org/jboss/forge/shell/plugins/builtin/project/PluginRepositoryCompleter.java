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

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class PluginRepositoryCompleter extends SimpleTokenCompleter
{
   @Inject
   private Project project;

   @Override
   public List<Object> getCompletionTokens()
   {
      MavenPluginFacet deps = project.getFacet(MavenPluginFacet.class);
      List<DependencyRepository> repositories = deps.getPluginRepositories();

      List<Object> result = new ArrayList<Object>();
      for (DependencyRepository dependencyRepository : repositories)
      {
         result.add(dependencyRepository.getUrl());
      }

      return result;
   }
}
