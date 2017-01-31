/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.File;
import java.nio.file.Path;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;

/**
 * A standard, built-in resource for representing files on the filesystem.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface FileResource<T extends FileResource<T>> extends Resource<File>,
         WriteableResource<FileResource<T>, File>
{
   /**
    * Return <code>true</code> if this {@link FileResource} exists and is actually a directory, otherwise return false;
    */
   public boolean isDirectory();

   /**
    * Returns <code>true</code> if the underlying resource has been modified on the file system since it was initially
    * loaded.
    * 
    * @return boolean true if resource is changed.
    */
   public boolean isStale();

   /**
    * Re-read the file-system meta-data for this resource (such as last modified time-stamp, and permissions.)
    */
   public void refresh();

   /**
    * Create a new single directory for this resource. This will not succeed if any parent directories needed for this
    * resource to exist are missing. You should consider using {@link #mkdirs()}
    */
   public boolean mkdir();

   /**
    * Create all directories required for this resource to exist.
    */
   public boolean mkdirs();

   /**
    * Requests that the file or directory denoted by this resource be deleted when the virtual machine terminates.
    * <p>
    * Once deletion has been requested, it is not possible to cancel the request. This method should therefore be used
    * with care.
    */
   public void deleteOnExit();

   /**
    * Create the file in the underlying resource system. Necessary directory paths will be created automatically.
    */
   public boolean createNewFile();

   /**
    * Create a temporary {@link FileResource}
    */
   public T createTempResource();

   /**
    * Rename this {@link Resource} to the given path.
    */
   public boolean renameTo(final String pathspec);

   /**
    * Rename this {@link Resource} to the given {@link FileResource}
    */
   public boolean renameTo(final FileResource<?> target);

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
    * A parent for a FileResource is always a DirectoryResource
    */
   @Override
   public DirectoryResource getParent();

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
   long getLastModified();

   /**
    * Set the last modified time-stamp of this resource.
    */
   void setLastModified(long currentTimeMillis);

   /**
    * Move this {@link Resource} to the given {@link FileResource}
    */
   void moveTo(FileResource<?> target);

   /**
    * Sets a file as writable
    */
   default void setWritable(boolean writable)
   {
      setWritable(writable, true);
   }

   /**
    * Sets a file as writable
    */
   void setWritable(boolean writable, boolean ownerOnly);

   /**
    * Sets a file as readable
    */
   default void setReadable(boolean readable)
   {
      setReadable(readable, true);
   }

   /**
    * Sets a file as readable
    */
   void setReadable(boolean readable, boolean owner);

   /**
    * Sets a file as executable
    */
   default void setExecutable(boolean executable)
   {
      setExecutable(executable, true);
   }

   /**
    * Sets a file as executable
    */
   void setExecutable(boolean executable, boolean owner);

   /**
    * Resolve a given resource based on its path
    *
    * @param type
    * @param path
    * @return <code>null</code> if no resource could be resolved for the given object.
    */
   Resource<File> resolve(final String path);

   /**
    * Resolve a given resource based on its path
    *
    * @param type
    * @param path
    * @return <code>null</code> if no resource could be resolved for the given object.
    */
   <TYPE extends Resource<File>> TYPE resolve(final Class<TYPE> type, final String path);

   /**
    * Resolve a {@link FileResource} from a given name.
    *
    * Implementations usually call {@link Path#resolve(Path)}
    *
    * @param class
    * @param name
    */
   @SuppressWarnings("unchecked")
   default FileResource<?> resolveAsFile(String name) throws ResourceException
   {
      return resolve(FileResource.class, name);
   }

   /**
    * Resolve a {@link DirectoryResource} from a given name.
    *
    * Implementations usually call {@link Path#resolve(Path)}
    *
    * @param class
    * @param name
    */
   default DirectoryResource resolveAsDirectory(String name) throws ResourceException
   {
      return resolve(DirectoryResource.class, name);
   }

}