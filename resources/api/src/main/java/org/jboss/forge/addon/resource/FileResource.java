package org.jboss.forge.addon.resource;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;

/**
 * A standard, built-in resource for representing files on the filesystem.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface FileResource<T extends FileResource<T>> extends Resource<File>
{
   /**
    * Return true if this {@link FileResource} exists and is actually a directory, otherwise return false;
    */
   public boolean isDirectory();

   /**
    * Returns true if the underlying resource has been modified on the file system since it was initially loaded.
    * 
    * @return boolean true if resource is changed.
    */
   public boolean isStale();

   /**
    * Re-read the last modified timestamp for this resource.
    */
   public void markUpToDate();

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
    * Set the contents of this {@link FileResource} to the given {@link String} using UTF-8 encoding.
    */
   public T setContents(String data);

   /**
    * Set the contents of this {@link FileResource} to the given {@link String} using the specified encoding.
    */
   public T setContents(String data, Charset charset);

   /**
    * Set the contents of this {@link FileResource} to the given character array using UTF-8 encoding.
    */
   public T setContents(final char[] data);

   /**
    * Set the contents of this {@link FileResource} to the given character array using the specified encoding.
    */
   public T setContents(final char[] data, Charset charset);

   /**
    * Set the contents of this {@link FileResource} to the contents of the given {@link InputStream}.
    */
   public T setContents(final InputStream data);

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
}