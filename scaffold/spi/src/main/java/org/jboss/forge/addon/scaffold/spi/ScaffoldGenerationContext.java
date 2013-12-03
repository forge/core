/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.spi;

import java.util.Collection;

/**
 * A context object for the scaffold generation.
 * 
 */
public class ScaffoldGenerationContext
{
   private String targetDirectory;
   private boolean overwrite;
   private Collection<?> resources;

   public ScaffoldGenerationContext(String targetDirectory, boolean overwrite, Collection<?> resources)
   {
      super();
      this.targetDirectory = targetDirectory == null ? "" : targetDirectory;
      this.overwrite = overwrite;
      this.resources = resources;
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
   
   public Collection<?> getResources()
   {
      return resources;
   }
   
   public void setResources(Collection<?> resources)
   {
      this.resources = resources;
   }

}