package org.jboss.forge.container.util;

import java.io.File;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Files
{
   public static final String HOME_ALIAS = "~";
   public static final String SLASH = File.separator;

   public static boolean delete(File file)
   {
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
      file.deleteOnExit();
   }

   public static boolean delete(File file, final boolean recursive)
   {
      if (recursive)
      {
         if (_deleteRecursive(file, true))
         {
            return true;
         }
         return false;
      }

      if ((file.listFiles() != null) && (file.listFiles().length != 0))
      {
         throw new RuntimeException("directory not empty");
      }

      if (OSUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      file.deleteOnExit(); // be paranoid
      if (file.delete())
      {
         return true;
      }
      return false;
   }

   private static boolean _deleteRecursive(final File file, final boolean collect)
   {
      if (collect && OSUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      if (file == null)
      {
         return false;
      }

      File[] children = file.listFiles();
      if (children != null)
      {
         for (File sf : children)
         {
            if (sf.isDirectory())
            {
               _deleteRecursive(sf, false);
            }
            else
            {
               if (!sf.delete())
               {
                  throw new RuntimeException("failed to delete: " + sf.getAbsolutePath());
               }
            }
         }
      }

      return file.delete();
   }

   /**
    * Replace instances of internal tokens with actual file equivalents.
    */
   public static String canonicalize(String target)
   {
      if (target.startsWith(Files.HOME_ALIAS))
      {
         String homePath = OSUtils.getUserHomePath();
         target = homePath + target.substring(1, target.length());
      }

      return target;
   }

   public static File getWorkingDirectory()
   {
      return new File("").getAbsoluteFile();
   }

}