package org.jboss.forge.addon.resource.zip;

import org.jboss.forge.addon.resource.DirectoryResource;

public interface ZipEntryResource
{

   /**
    * Extract entry
    */
   void extract(DirectoryResource extractToDirectory);
}
