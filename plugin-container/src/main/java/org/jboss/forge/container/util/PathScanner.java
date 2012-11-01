package org.jboss.forge.container.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.weld.environment.se.discovery.url.ClasspathScanningException;

public class PathScanner
{
   public static Set<String> scan(ClassLoader loader, Set<String> paths)
   {
      try
      {
         List<String> discoveredClasses = new ArrayList<String>();
         Set<String> result = new HashSet<String>();
         // grab all the URLs for this resource
         Set<URL> urls = new HashSet<URL>();
         for (String path : paths)
         {
            urls.addAll(Collections.list(loader.getResources(path)));
         }
         for (URL url : urls)
         {
            String urlPath = url.toExternalForm();

            // determine resource type (eg: jar, file, bundle)
            String urlType = "file";
            int colonIndex = urlPath.indexOf(":");
            if (colonIndex != -1)
            {
               urlType = urlPath.substring(0, colonIndex);
            }

            // Extra built-in support for simple file-based resources
            if ("file".equals(urlType) || "jar".equals(urlType))
            {
               // switch to using getPath() instead of toExternalForm()
               urlPath = url.getPath();

               if (urlPath.indexOf('!') > 0)
               {
                  urlPath = urlPath.substring(0, urlPath.indexOf('!'));
               }
               // else
               // {
               // // hack for /META-INF/beans.xml
               // File dirOrArchive = new File(urlPath);
               // if ((urlPath != null) && (urlPath.lastIndexOf('/') > 0))
               // {
               // dirOrArchive = dirOrArchive.getParentFile();
               // }
               // urlPath = dirOrArchive.toString();
               // }
            }

            try
            {
               urlPath = URLDecoder.decode(urlPath, "UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
               throw new ClasspathScanningException("Error decoding URL using UTF-8");
            }

            result.add(urlPath);

            handle(result, discoveredClasses);
         }

         result.clear();
         for (String className : discoveredClasses)
         {
            result.add(getPackage(className));
         }

         return result;
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to scan ClassLoader [" + loader + "]", e);
      }
   }

   private static String getPackage(String className)
   {
      return className.substring(0, className.lastIndexOf(".")).replaceAll("\\.", "/");
   }

   public static void handle(Collection<String> paths, List<String> discoveredClasses)
   {
      for (String urlPath : paths)
      {
         try
         {
            if (urlPath.startsWith("file:"))
            {
               urlPath = urlPath.substring(5);
            }
            if (urlPath.indexOf('!') > 0)
            {
               urlPath = urlPath.substring(0, urlPath.indexOf('!'));
            }

            File file = new File(urlPath);
            if (file.isDirectory())
            {
               handleDirectory(file, null, discoveredClasses);
            }
            else
            {
               handleArchiveByFile(file, discoveredClasses);
            }
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   private static void handleArchiveByFile(File file, List<String> discoveredClasses) throws IOException
   {
      try
      {
         String archiveUrl = "jar:" + file.toURI().toURL().toExternalForm() + "!/";
         ZipFile zip = new ZipFile(file);
         Enumeration<? extends ZipEntry> entries = zip.entries();

         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            handle(name, new URL(archiveUrl + name), discoveredClasses);
         }
      }
      catch (ZipException e)
      {
         throw new RuntimeException("Error handling file " + file, e);
      }
   }

   private static void handleDirectory(File file, String path, List<String> discoveredClasses)
   {
      for (File child : file.listFiles())
      {
         String newPath = (path == null) ? child.getName() : (path + '/' + child.getName());

         if (child.isDirectory())
         {
            handleDirectory(child, newPath, discoveredClasses);
         }
         else
         {
            try
            {
               handle(newPath, child.toURI().toURL(), discoveredClasses);
            }
            catch (MalformedURLException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   protected static void handle(String name, URL url, List<String> discoveredClasses)
   {
      if (name.endsWith(".class"))
      {
         String className = filenameToClassname(name);
         try
         {
            discoveredClasses.add(className);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Convert a path to a class file to a class name
    */
   public static String filenameToClassname(String filename)
   {
      return filename.substring(0, filename.lastIndexOf(".class")).replace('/', '.').replace('\\', '.');
   }
}
