package org.jboss.forge.furnace.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Files
{
   public static final String HOME_ALIAS = "~";
   public static final String SLASH = File.separator;
   /**
    * The number of bytes in a kilobyte.
    */
   public static final long ONE_KB = 1024;

   /**
    * The number of bytes in a megabyte.
    */
   public static final long ONE_MB = ONE_KB * ONE_KB;

   /**
    * The number of bytes in a 50 MB.
    */
   private static final long FIFTY_MB = ONE_MB * 50;

   /**
    * The number of bytes in a gigabyte.
    */
   public static final long ONE_GB = ONE_KB * ONE_MB;

   public static boolean delete(File file)
   {
      Assert.notNull(file, "File to delete must not be null.");
      return delete(file, false);
   }

   /**
    * Requests that the file or directory denoted by this resource be deleted when the virtual machine terminates.
    * <p>
    * Once deletion has been requested, it is not possible to cancel the request. This method should therefore be used
    * with care.
    */
   public static void deleteOnExit(File file)
   {
      Assert.notNull(file, "File to delete must not be null.");
      file.deleteOnExit();
   }

   public static boolean delete(File file, final boolean recursive)
   {
      Assert.notNull(file, "File to delete must not be null.");

      boolean result = false;
      if (recursive)
      {
         result = _deleteRecursive(file, true);
      }
      else
      {
         if ((file.listFiles() != null) && (file.listFiles().length != 0))
         {
            throw new RuntimeException("directory not empty");
         }

         if (OperatingSystemUtils.isWindows())
         {
            System.gc(); // ensure no lingering handles that would prevent deletion
         }

         file.deleteOnExit(); // be paranoid
         result = file.delete();
      }
      return result;
   }

   private static boolean _deleteRecursive(final File file, final boolean collect)
   {
      Assert.notNull(file, "File to delete must not be null.");

      boolean result = true;
      if (collect && OperatingSystemUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      File[] children = file.listFiles();
      if (children != null)
      {
         for (File sf : children)
         {
            if (sf.isDirectory())
            {
               if (!_deleteRecursive(sf, false))
                  result = false;
            }
            else
            {
               sf.deleteOnExit(); // be paranoid
               if (!sf.delete())
                  result = false;
            }
         }
      }

      file.deleteOnExit(); // be paranoid
      return file.delete() && result;
   }

   /**
    * Replace instances of internal tokens with actual file equivalents.
    */
   public static String canonicalize(String target)
   {
      if (target.startsWith(Files.HOME_ALIAS))
      {
         String homePath = OperatingSystemUtils.getUserHomePath();
         target = homePath + target.substring(1, target.length());
      }

      return target;
   }

   public static File getWorkingDirectory()
   {
      return new File("").getAbsoluteFile();
   }

   /**
    * Copies a file to a directory optionally preserving the file date.
    * <p>
    * This method copies the contents of the specified source file to a file of the same name in the specified
    * destination directory. The destination directory is created if it does not exist. If the destination file exists,
    * then this method will overwrite it.
    * <p>
    * <strong>Note:</strong> Setting <code>preserveFileDate</code> to <code>true</code> tries to preserve the file's
    * last modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that the operation
    * will succeed. If the modification operation fails, no indication is provided.
    * 
    * @param srcFile an existing file to copy, must not be <code>null</code>
    * @param destDir the directory to place the copy in, must not be <code>null</code>
    * 
    * @throws NullPointerException if source or destination is <code>null</code>
    * @throws IOException if source or destination is invalid
    * @throws IOException if an IO error occurs during copying
    * @see #copyFile(File, File)
    * @since Commons IO 1.3
    */
   public static void copyFileToDirectory(File srcFile, File destDir) throws IOException
   {
      if (destDir == null)
      {
         throw new NullPointerException("Destination must not be null");
      }
      if (destDir.exists() && destDir.isDirectory() == false)
      {
         throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
      }
      File destFile = new File(destDir, srcFile.getName());
      copyFile(srcFile, destFile);
   }

   /**
    * Copies a file to a new location.
    * <p>
    * This method copies the contents of the specified source file to the specified destination file. The directory
    * holding the destination file is created if it does not exist. If the destination file exists, then this method
    * will overwrite it.
    * 
    * @param srcFile an existing file to copy, must not be <code>null</code>
    * @param destFile the new file, must not be <code>null</code>
    * @param preserveFileDate true if the file date of the copy should be the same as the original
    * 
    * @throws NullPointerException if source or destination is <code>null</code>
    * @throws IOException if source or destination is invalid
    * @throws IOException if an IO error occurs during copying
    * @see #copyFileToDirectory(File, File, boolean)
    */
   public static void copyFile(File srcFile, File destFile) throws IOException
   {
      if (srcFile == null)
      {
         throw new NullPointerException("Source must not be null");
      }
      if (destFile == null)
      {
         throw new NullPointerException("Destination must not be null");
      }
      if (srcFile.exists() == false)
      {
         throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
      }
      if (srcFile.isDirectory())
      {
         throw new IOException("Source '" + srcFile + "' exists but is a directory");
      }
      if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
      {
         throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
      }
      if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false)
      {
         if (destFile.getParentFile().mkdirs() == false)
         {
            throw new IOException("Destination '" + destFile + "' directory cannot be created");
         }
      }
      if (destFile.exists() && destFile.canWrite() == false)
      {
         throw new IOException("Destination '" + destFile + "' exists but is read-only");
      }
      doCopyFile(srcFile, destFile);
   }

   /**
    * Internal copy file method.
    * 
    * @param srcFile the validated source file, must not be <code>null</code>
    * @param destFile the validated destination file, must not be <code>null</code>
    * @throws IOException if an error occurs
    */
   @SuppressWarnings("resource")
   private static void doCopyFile(File srcFile, File destFile) throws IOException
   {
      if (destFile.exists() && destFile.isDirectory())
      {
         throw new IOException("Destination '" + destFile + "' exists but is a directory");
      }

      FileInputStream fis = null;
      FileOutputStream fos = null;
      FileChannel input = null;
      FileChannel output = null;
      try
      {
         fis = new FileInputStream(srcFile);
         fos = new FileOutputStream(destFile);
         input = fis.getChannel();
         output = fos.getChannel();
         long size = input.size();
         long pos = 0;
         long count = 0;
         while (pos < size)
         {
            count = (size - pos) > FIFTY_MB ? FIFTY_MB : (size - pos);
            pos += output.transferFrom(input, pos, count);
         }
      }
      finally
      {
         Streams.closeQuietly(output);
         Streams.closeQuietly(fos);
         Streams.closeQuietly(input);
         Streams.closeQuietly(fis);
      }

      if (srcFile.length() != destFile.length())
      {
         throw new IOException("Failed to copy full contents from '" +
                  srcFile + "' to '" + destFile + "'");
      }
   }

   /**
    * Copies a whole directory to a new location.
    * <p>
    * This method copies the contents of the specified source directory to within the specified destination directory.
    * <p>
    * The destination directory is created if it does not exist. If the destination directory did exist, then this
    * method merges the source with the destination, with the source taking precedence.
    * <p>
    * <strong>Note:</strong> Setting <code>preserveFileDate</code> to {@code true} tries to preserve the files' last
    * modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that those operations
    * will succeed. If the modification operation fails, no indication is provided.
    * 
    * @param srcDir an existing directory to copy, must not be {@code null}
    * @param destDir the new directory, must not be {@code null}
    * 
    * @throws NullPointerException if source or destination is {@code null}
    * @throws IOException if source or destination is invalid
    * @throws IOException if an IO error occurs during copying
    * @since 1.1
    */
   public static void copyDirectory(File srcDir, File destDir) throws IOException
   {
      copyDirectory(srcDir, destDir, null);
   }

   /**
    * Copies a filtered directory to a new location.
    * <p>
    * This method copies the contents of the specified source directory to within the specified destination directory.
    * <p>
    * The destination directory is created if it does not exist. If the destination directory did exist, then this
    * method merges the source with the destination, with the source taking precedence.
    * <p>
    * <strong>Note:</strong> Setting <code>preserveFileDate</code> to {@code true} tries to preserve the files' last
    * modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that those operations
    * will succeed. If the modification operation fails, no indication is provided.
    * 
    * <h4>Example: Copy directories only</h4>
    * 
    * <pre>
    * // only copy the directory structure
    * FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, false);
    * </pre>
    * 
    * <h4>Example: Copy directories and txt files</h4>
    * 
    * <pre>
    * // Create a filter for &quot;.txt&quot; files
    * IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(&quot;.txt&quot;);
    * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
    * 
    * // Create a filter for either directories or &quot;.txt&quot; files
    * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
    * 
    * // Copy using the filter
    * FileUtils.copyDirectory(srcDir, destDir, filter, false);
    * </pre>
    * 
    * @param srcDir an existing directory to copy, must not be {@code null}
    * @param destDir the new directory, must not be {@code null}
    * @param filter the filter to apply, null means copy all directories and files
    * @param preserveFileDate true if the file date of the copy should be the same as the original
    * 
    * @throws NullPointerException if source or destination is {@code null}
    * @throws IOException if source or destination is invalid
    * @throws IOException if an IO error occurs during copying
    * @since 1.4
    */
   public static void copyDirectory(File srcDir, File destDir,
            FileFilter filter) throws IOException
   {
      if (srcDir == null)
      {
         throw new NullPointerException("Source must not be null");
      }
      if (destDir == null)
      {
         throw new NullPointerException("Destination must not be null");
      }
      if (srcDir.exists() == false)
      {
         throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
      }
      if (srcDir.isDirectory() == false)
      {
         throw new IOException("Source '" + srcDir + "' exists but is not a directory");
      }
      if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath()))
      {
         throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
      }

      // Cater for destination being directory within the source directory (see IO-141)
      List<String> exclusionList = null;
      if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath()))
      {
         File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
         if (srcFiles != null && srcFiles.length > 0)
         {
            exclusionList = new ArrayList<String>(srcFiles.length);
            for (File srcFile : srcFiles)
            {
               File copiedFile = new File(destDir, srcFile.getName());
               exclusionList.add(copiedFile.getCanonicalPath());
            }
         }
      }
      doCopyDirectory(srcDir, destDir, filter, exclusionList);
   }

   /**
    * Internal copy directory method.
    * 
    * @param srcDir the validated source directory, must not be {@code null}
    * @param destDir the validated destination directory, must not be {@code null}
    * @param filter the filter to apply, null means copy all directories and files
    * @param preserveFileDate whether to preserve the file date
    * @param exclusionList List of files and directories to exclude from the copy, may be null
    * @throws IOException if an error occurs
    */
   private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
            List<String> exclusionList) throws IOException
   {
      // recurse
      File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
      if (srcFiles == null)
      { // null if abstract pathname does not denote a directory, or if an I/O error occurs
         throw new IOException("Failed to list contents of " + srcDir);
      }
      if (destDir.exists())
      {
         if (destDir.isDirectory() == false)
         {
            throw new IOException("Destination '" + destDir + "' exists but is not a directory");
         }
      }
      else
      {
         if (!destDir.mkdirs() && !destDir.isDirectory())
         {
            throw new IOException("Destination '" + destDir + "' directory cannot be created");
         }
      }
      if (destDir.canWrite() == false)
      {
         throw new IOException("Destination '" + destDir + "' cannot be written to");
      }
      for (File srcFile : srcFiles)
      {
         File dstFile = new File(destDir, srcFile.getName());
         if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath()))
         {
            if (srcFile.isDirectory())
            {
               doCopyDirectory(srcFile, dstFile, filter, exclusionList);
            }
            else
            {
               doCopyFile(srcFile, dstFile);
            }
         }
      }
   }

}