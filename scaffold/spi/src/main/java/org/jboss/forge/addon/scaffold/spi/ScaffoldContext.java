/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.spi;

import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * A context object for the scaffold
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ScaffoldContext
{
   private final DirectoryResource targetDirectory;
   private final boolean overwrite;

   public ScaffoldContext(DirectoryResource targetDirectory, boolean overwrite)
   {
      super();
      this.targetDirectory = targetDirectory;
      this.overwrite = overwrite;
   }

   public DirectoryResource getTargetDirectory()
   {
      return targetDirectory;
   }

   public boolean isOverwrite()
   {
      return overwrite;
   }

}
