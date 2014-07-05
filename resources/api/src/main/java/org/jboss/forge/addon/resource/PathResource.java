package org.jboss.forge.addon.resource;

import java.nio.file.Path;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;

/**
 * A resource backed by a NIO2 Path object
 *
 * @author Shane Bryzak
 *
 */
public interface PathResource extends Resource<Path>,
         CreatableResource<PathResource, Path>,
         WriteableResource<PathResource, Path>
{
   /**
    * Returns true if the underlying resource has been modified on the file system since it was initially loaded.
    *
    * @return boolean true if resource is changed.
    */
   public boolean isStale();

   /**
    * Re-read the file-system meta-data for this resource (such as last modified time-stamp, and permissions.)
    */
   public void refresh();

   /**
    * Requests that the file or directory denoted by this resource be deleted when the virtual machine terminates.
    * <p>
    * Once deletion has been requested, it is not possible to cancel the request. This method should therefore be used
    * with care.
    */
   public void deleteOnExit();

   /**
    * Rename this {@link Resource} to the given path.
    */
   public boolean renameTo(final String pathspec);

   /**
    * Rename this {@link Resource} to the given {@link FileResource}
    */
   public boolean renameTo(final PathResource target);

   /**
    * Returns the size of the file denoted by this abstract pathname
    */
   public long getSize();

   /**
    * Returns if a file is writable
    */
   public boolean isWritable();

   /**
    * Returns if a file is readable
    */
   public boolean isReadable();

   /**
    * Returns if a file is executable
    */
   public boolean isExecutable();

   /**
    * A parent for a PathResource is always another PathResource
    */
   @Override
   public PathResource getParent();

   /**
    * Monitors this FileResource
    */
   ResourceMonitor monitor();

   /**
    * Monitors this FileResource using the given filter
    */
   ResourceMonitor monitor(ResourceFilter filter);

   /**
    * Get the last modified time-stamp of this resource.
    */
   public long getLastModified();

   /**
    * Set the last modified time-stamp of this resource.
    */
   public void setLastModified(long currentTimeMillis);
}
