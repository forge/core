/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.util.Assert;

/**
 * A simple in-memory {@link ProjectCache}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class InMemoryProjectCache implements ProjectCache
{
   private final Map<String, Project> projects = new ConcurrentHashMap<>();

   @Override
   public Project get(Resource<?> root)
   {
      Assert.notNull(root, "Resource should not be null");
      Project project = projects.get(root.getFullyQualifiedName());
      return project;
   }

   @Override
   public void invalidate()
   {
      this.projects.clear();
   }

   @Override
   public void store(Project project)
   {
      Assert.notNull(project, "Project should not be null");
      this.projects.put(project.getRoot().getFullyQualifiedName(), project);
   }

   @Override
   public void evict(Project project)
   {
      this.projects.remove(project.getRoot().getFullyQualifiedName());
   }

}
