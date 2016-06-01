/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.zip;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * A {@link Resource} that represents a ZipFileEntry
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ZipFileResourceEntry extends Resource<String>
{
   /**
    * Unzip this {@link ZipFileResourceEntry} to the specified {@link DirectoryResource}.
    * 
    * This method will extract the full path, including root folders from this {@link ZipFileResourceEntry}
    * 
    * @param directoryResource the target directory
    */
   void extractTo(DirectoryResource directoryResource);

   /**
    * Unzip this {@link ZipFileResourceEntry} to the specified {@link DirectoryResource}.
    * 
    * @param directoryResource the target directory
    * @param newName the new file name
    */
   void extractTo(DirectoryResource directoryResource, String newName);

   /**
    * @return <code>true</code> if this {@link ZipFileResourceEntry} is a directory
    */
   boolean isDirectory();
}
