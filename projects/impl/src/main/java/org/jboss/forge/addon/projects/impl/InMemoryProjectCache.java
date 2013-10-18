/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.furnace.util.Assert;

/**
 * A simple in-memory {@link ProjectCache}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class InMemoryProjectCache implements ProjectCache
{
   private final Map<String, WeakReference<Project>> projects = new ConcurrentHashMap<String, WeakReference<Project>>();

   @Override
   public Project get(DirectoryResource dir)
   {
      Assert.notNull(dir, "Directory Resource should not be null");

      WeakReference<Project> ref = projects.get(dir.getFullyQualifiedName());

      Project project = null;
      if (ref != null)
         project = ref.get();

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
      this.projects.put(project.getProjectRoot().getFullyQualifiedName(), new WeakReference<Project>(project));
   }

   @Override
   public void evict(Project project)
   {
      this.projects.remove(project.getProjectRoot().getFullyQualifiedName());
   }

}
