package org.jboss.forge.addon.database.tools.util;

import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.forge.furnace.util.Streams;

public class UrlClassLoaderExecutor
{
   public static void execute(URL[] urls, Runnable runnable)
   {
      ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
      URLClassLoader newClassLoader = null;
      try
      {
         newClassLoader = new URLClassLoader(urls, savedClassLoader);
         Thread.currentThread().setContextClassLoader(newClassLoader);
         runnable.run();
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(savedClassLoader);
         Streams.closeQuietly(newClassLoader);
      }
   }
}
