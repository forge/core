package org.jboss.forge.container.modules.providers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.forge.container.modules.ModuleSpecProvider;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;

public class JavaxApiSpec implements ModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("javax.api");

   private static Set<String> paths = new HashSet<String>();

   static
   {

      ClassLoader loader = ClassLoader.getSystemClassLoader();
      URL[] urls = ((URLClassLoader) loader).getURLs();

      for (URL url : urls)
      {
         try
         {
            File file = new File(url.toURI());
            if (file.isDirectory())
            {
               paths.addAll(getPathsFrom(file.getAbsolutePath(), file));
            }
            else
            {
               JarFile jar = new JarFile(file);
               Enumeration<JarEntry> entries = jar.entries();
               while (entries.hasMoreElements())
               {
                  JarEntry entry = entries.nextElement();
                  String name = entry.getName();
                  if (name.indexOf('/') != -1)
                     paths.add(name.substring(0, name.lastIndexOf('/')));
               }
            }
         }
         catch (IOException e)
         {
            System.out.println("Failed loading paths from: [" + url.toString() + "]. Attempting folder discovery");
         }
         catch (URISyntaxException e)
         {
            throw new RuntimeException(e);
         }
      }

      for (String path : paths)
      {
         System.out.println(path);
      }
   }

   @Override
   public ModuleSpec get(ModuleIdentifier id)
   {
      if (ID.equals(id))
      {
         Builder builder = ModuleSpec.build(id);
         builder.addDependency(DependencySpec.createSystemDependencySpec(paths, true));
         return builder.create();
      }
      return null;
   }

   private static Set<String> getPathsFrom(String root, File file)
   {
      Set<String> result = new HashSet<String>();
      String[] children = file.list();
      for (String name : children)
      {
         File child = new File(file.getAbsolutePath() + "/" + name);
         if (child.isDirectory())
         {
            result.addAll(getPathsFrom(root, child));
            String path = child.getAbsolutePath().substring(root.length() + 1);
            result.add(path);
         }
      }
      return result;
   }

}
