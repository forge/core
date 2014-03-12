/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.spi;

import org.jboss.forge.addon.resource.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A context object for the scaffold generation.
 * 
 */
public class ScaffoldGenerationContext
{
   private String targetDirectory;
   private boolean overwrite;
   private Collection<Resource<?>> resources;
   private Map<String, Object> attributes;

   public ScaffoldGenerationContext(String targetDirectory, boolean overwrite, Collection<Resource<?>> resources)
   {
      super();
      this.targetDirectory = targetDirectory == null ? "" : targetDirectory;
      this.overwrite = overwrite;
      this.resources = resources;
      this.attributes = new HashMap<String, Object>();
   }

   public String getTargetDirectory()
   {
      return targetDirectory;
   }
   
   public void setTargetDirectory(String targetDirectory)
   {
      this.targetDirectory = targetDirectory;
   }

   public boolean isOverwrite()
   {
      return overwrite;
   }
   
   public void setOverwrite(boolean overwrite)
   {
      this.overwrite = overwrite;
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

}