package org.jboss.forge.addon.resource.monitor;

import java.lang.reflect.Field;
import java.nio.file.WatchKey;

import sun.misc.Cleaner;

/**
 * Workaround for JDK bug #8029516 : https://bugs.openjdk.java.net/browse/JDK-8029516
 *
 * "Workaround for Java bug which causes crash dump" by apangin @ Stackoverflow: http://stackoverflow.com/a/23450366 is
 * licensed under CC BY-SA 3.0. *
 */
@SuppressWarnings("restriction")
class JDK_8029516
{
   private static final Field bufferField = getField("sun.nio.fs.WindowsWatchService$WindowsWatchKey", "buffer");
   private static final Field cleanerField = getField("sun.nio.fs.NativeBuffer", "cleaner");
   private static final Cleaner dummyCleaner = Cleaner.create(Thread.class, new Thread());

   private static Field getField(String className, String fieldName)
   {
      try
      {
         Field f = Class.forName(className).getDeclaredField(fieldName);
         f.setAccessible(true);
         return f;
      }
      catch (Exception e)
      {
         throw new IllegalStateException(e);
      }
   }

   public static void patch(WatchKey key)
   {
      try
      {
         cleanerField.set(bufferField.get(key), dummyCleaner);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalStateException(e);
      }
   }
}
