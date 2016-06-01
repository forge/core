/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;

/**
 * A context object for the scaffold generation.
 * 
 */
public class ScaffoldGenerationContext
{
   private String targetDirectory;
   private Collection<Resource<?>> resources;
   private Map<String, Object> attributes;
   private Project project;

   public ScaffoldGenerationContext(String targetDirectory, Collection<Resource<?>> resources,
            Project project)
   {
      super();
      this.targetDirectory = targetDirectory == null ? "" : targetDirectory;
      this.resources = resources;
      this.attributes = new HashMap<>();
      this.project = project;
   }

   public String getTargetDirectory()
   {
      return targetDirectory;
   }

   public void setTargetDirectory(String targetDirectory)
   {
      this.targetDirectory = targetDirectory;
   }

   public Collection<Resource<?>> getResources()
   {
      return resources;
   }

   public void setResources(Collection<Resource<?>> resources)
   {
      this.resources = resources;
   }

   public Object getAttribute(String key)
   {
      return attributes.get(key);
   }

   public void addAttribute(String key, Object value)
   {
      attributes.put(key, value);
   }

   public void removeAttribute(String key)
   {
      attributes.remove(key);
   }

   public Project getProject()
   {
      return project;
   }

   public void setProject(Project project)
   {
      this.project = project;
   }
}