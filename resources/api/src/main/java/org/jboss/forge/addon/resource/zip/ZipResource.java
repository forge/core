package org.jboss.forge.addon.resource.zip;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Represents a .zip file.
 * @author mbriskar
 *
 */
public interface ZipResource extends FileResource<ZipResource>
{
   /**
    * Unzip the zip file into the directory resource
    * @param dir Directory resource to which the zip file will be unzipped
    */
   void unzipAll(DirectoryResource dir);
   
}
