package org.jboss.forge.furnace.modules;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.weld.environment.se.discovery.url.ClasspathScanningException;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * Scan the classloader
 * 
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 * @author Pete Muir
 * @author Peter Royle
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ModularURLScanner
{
   private final String[] resources;
   private final ResourceLoader resourceLoader;

   public ModularURLScanner(ResourceLoader resourceLoader, String... resources)
   {
      this.resources = resources;
      this.resourceLoader = resourceLoader;
   }

   public ModuleScanResult scan()
   {
      List<String> discoveredClasses = new ArrayList<String>();
      List<URL> discoveredResourceUrls = new ArrayList<URL>();
      Collection<String> paths = new ArrayList<String>();
      for (String resourceName : resources)
      {
         // grab all the URLs for this resource
         Collection<URL> urlEnum = resourceLoader.getResources(resourceName);
         for (URL url : urlEnum)
         {

            String urlPath = url.toExternalForm();

            // determin resource type (eg: jar, file, bundle)
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
               else
               {
                  // hack for /META-INF/beans.xml
                  File dirOrArchive = new File(urlPath);
                  if ((resourceName != null) && (resourceName.lastIndexOf('/') > 0))
                  {
                     dirOrArchive = dirOrArchive.getParentFile();
                  }
                  urlPath = dirOrArchive.getParent();
               }
            }

            try
            {
               urlPath = URLDecoder.decode(urlPath, "UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
               throw new ClasspathScanningException("Error decoding URL using UTF-8");
            }

            paths.add(urlPath);
         }

         ModularFileSystemURLHandler handler = new ModularFileSystemURLHandler(resourceLoader);
         handler.handle(paths, discoveredClasses, discoveredResourceUrls);
      }

      return new ModuleScanResult(resourceLoader, discoveredResourceUrls, discoveredClasses);
   }

}
