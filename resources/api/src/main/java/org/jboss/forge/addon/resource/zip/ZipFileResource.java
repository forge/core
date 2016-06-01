/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.zip;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * Handles a Zip file and provide operations related to it
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ZipFileResource extends FileResource<ZipFileResource>
{
   /**
    * Unzip all files to the specified {@link DirectoryResource}.
    * 
    * @param directoryResource the target directory
    */
   void extractTo(DirectoryResource directoryResource);

   /**
    * Add the specified resources to this Zip file
    * 
    * @param resources the {@link FileResource} instances to be added
    * @return this {@link ZipFileResource} instance, for method chaining
    */
   ZipFileResource add(FileResource<?>... resources);

   /**
    * Add the specified {@link Resource} to this Zip file with the given name
    * 
    * @param name the file name inside the Zip
    * @param resource the {@link Resource} instance to be added
    * @return this {@link ZipFileResource} instance, for method chaining
    */
   ZipFileResource add(String name, Resource<?> resource);

   /**
    * Sets the password for this {@link ZipFileResource}
    * 
    * @param password the password to be used when reading this file
    * @return this {@link ZipFileResource} instance, for method chaining
    */
   ZipFileResource setPassword(char[] password);

   /**
    * @return true if this Zip file is encrypted
    */
   boolean isEncrypted();
}
