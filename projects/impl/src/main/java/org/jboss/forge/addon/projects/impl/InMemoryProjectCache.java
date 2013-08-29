/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * A simple in-memory {@link ProjectCache}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class InMemoryProjectCache implements ProjectCache
{
   private Map<String, Project> projects = new ConcurrentHashMap<String, Project>();

   @Override
   public Project get(DirectoryResource dir)
   {
      return projects.get(dir.getFullyQualifiedName());
   }

   @Override
   public void invalidate()
   {
      this.projects.clear();
   }

   @Override
   public void store(Project project)
   {
      this.projects.put(project.getProjectRoot().getFullyQualifiedName(), project);
   }

}
