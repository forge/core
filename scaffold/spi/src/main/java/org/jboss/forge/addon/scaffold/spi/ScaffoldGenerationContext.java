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
   private final String targetDirectory;
   private final boolean overwrite;
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

   public boolean isOverwrite()
   {
      return overwrite;
   }
   
   public Collection<?> getResources()
   {
      return resources;
   }

}