package org.jboss.forge.furnace.se;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class BootstrapClassLoader extends URLClassLoader
{
   public BootstrapClassLoader(String bootstrapPath)
   {
      super(new JarLocator(bootstrapPath).find(), null);
   }

   private static class JarLocator
   {
      private String path;

      public JarLocator(String path)
      {
         this.path = path;
      }

      private URL[] find()
      {
         List<URL> result = new ArrayList<URL>();
         try
         {
            for (URL url : Collections.list(JarLocator.class.getClassLoader().getResources(path)))
            {
               String urlPath = url.getFile();
               urlPath = URLDecoder.decode(urlPath, "UTF-8");
               if (urlPath.startsWith("file:"))
               {
                  urlPath = urlPath.substring(5);
               }
               if (urlPath.indexOf('!') > 0)
               {
                  urlPath = urlPath.substring(0, urlPath.indexOf('!'));
               }
               result.add(new URL(url, "."));
               result.addAll(handle(urlPath, url));
            }
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not load jars from " + path, e);
         }

         return result.toArray(new URL[0]);
      }

      private List<URL> handle(String urlPath, URL original) throws IOException
      {
         File file = new File(urlPath);
         if (file.exists())
            if (file.isDirectory())
            {
               return handle(file);
            }
            else
            {
               return handleArchiveByFile(file, original);
            }
         else
            throw new FileNotFoundException(urlPath);
      }

      private List<URL> handle(File file)
      {
         List<URL> result = new ArrayList<URL>();
         for (File child : file.listFiles())
         {
            if (!child.isDirectory() && child.getName().endsWith(".jar"))
            {
               try
               {
                  result.add(child.toURI().toURL());
               }
               catch (MalformedURLException e)
               {
                  throw new RuntimeException("Could not convert to URL " + child, e);
               }
            }
         }
         return result;
      }

      @SuppressWarnings("deprecation")
      private List<URL> handleArchiveByFile(File file, URL url) throws IOException
      {
         List<URL> result = new ArrayList<URL>();
         try
         {
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements())
            {
               ZipEntry entry = entries.nextElement();
               String name = entry.getName();
               if (name.matches(path + "/.*\\.jar"))
               {
                  result.add(
                           copy(entry.getName(),
                                    JarLocator.class.getClassLoader().getResource(name).openStream()
                           ).toURL()
                           );
               }
            }
         }
         catch (ZipException e)
         {
            throw new RuntimeException("Error handling file " + file, e);
         }
         return result;
      }

      private File copy(String name, InputStream input)
      {
         File outputFile = new File("target", name);
         outputFile.getParentFile().mkdirs();

         FileOutputStream output = null;
         try
         {
            output = new FileOutputStream(outputFile);
            final byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = input.read(buffer)) != -1)
            {
               output.write(buffer, 0, read);
            }
            output.flush();
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not write out jar file " + name, e);
         }
         finally
         {
            close(input);
            close(output);
         }
         return outputFile;
      }

      private void close(Closeable closeable)
      {
         try
         {
            if (closeable != null)
            {
               closeable.close();
            }
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not close stream", e);
         }
      }
   }
}
