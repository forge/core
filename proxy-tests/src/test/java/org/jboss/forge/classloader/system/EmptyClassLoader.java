package org.jboss.forge.classloader.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class EmptyClassLoader extends ClassLoader
{
   @Override
   public synchronized void clearAssertionStatus()
   {
      super.clearAssertionStatus();
   }

   @Override
   protected Package definePackage(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5,
            String arg6, URL arg7) throws IllegalArgumentException
   {
      return getClass().getPackage();
   }

   @Override
   protected Class<?> findClass(String arg0) throws ClassNotFoundException
   {
      if (arg0.startsWith("java.lang"))
         return super.findClass(arg0);
      throw new ClassNotFoundException();
   }

   @Override
   protected String findLibrary(String arg0)
   {
      return null;
   }

   @Override
   protected URL findResource(String arg0)
   {
      return null;
   }

   @Override
   protected Enumeration<URL> findResources(String arg0) throws IOException
   {
      return Collections.enumeration(Arrays.asList(new URL[] {}));
   }

   @Override
   protected Package getPackage(String arg0)
   {
      return null;
   }

   @Override
   protected Package[] getPackages()
   {
      return new Package[] {};
   }

   @Override
   public URL getResource(String arg0)
   {
      return null;
   }

   @Override
   public InputStream getResourceAsStream(String arg0)
   {
      return null;
   }

   @Override
   public Enumeration<URL> getResources(String arg0) throws IOException
   {
      return Collections.enumeration(Arrays.asList(new URL[] {}));
   }

   @Override
   protected synchronized Class<?> loadClass(String arg0, boolean arg1) throws ClassNotFoundException
   {
      if (arg0.startsWith("org.jboss"))
         return super.loadClass(arg0, arg1);
      if (arg0.startsWith("java.lang"))
         return super.loadClass(arg0, arg1);
      throw new ClassNotFoundException();
   }

   @Override
   public Class<?> loadClass(String arg0) throws ClassNotFoundException
   {
      return loadClass(arg0, false);
   }

   @Override
   public synchronized void setClassAssertionStatus(String arg0, boolean arg1)
   {
   }

   @Override
   public synchronized void setDefaultAssertionStatus(boolean arg0)
   {
   }

   @Override
   public synchronized void setPackageAssertionStatus(String arg0, boolean arg1)
   {
   }
}