/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.monitor;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Uses {@link WatchService} to watch files
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class FileWatcher implements Runnable
{
   private static Logger log = Logger.getLogger(FileWatcher.class.getName());

   private final WatchService watcher;
   private final Map<WatchKey, ResourceMonitorImpl> keys = new ConcurrentHashMap<>();
   private Thread resourceMonitorThread;
   private volatile boolean alive = true;

   public FileWatcher() throws IOException
   {
      this.watcher = FileSystems.getDefault().newWatchService();
   }

   public void start() throws IllegalStateException
   {
      if (!this.alive)
      {
         throw new IllegalStateException("FileWatcher already stopped");
      }
      resourceMonitorThread = new Thread(this, "Resource File Monitor");
      resourceMonitorThread.setDaemon(true);
      resourceMonitorThread.setContextClassLoader(null);
      resourceMonitorThread.start();
   }

   public void stop()
   {
      this.alive = false;
      resourceMonitorThread.interrupt();
      try
      {
         this.watcher.close();
      }
      catch (IOException e)
      {
         log.log(Level.WARNING, "Error while closing FileWatcher", e);
      }
   }

   /**
    * Register the given directory with the WatchService
    */
   void register(ResourceMonitorImpl monitorImpl) throws IOException
   {
      Path path = monitorImpl.getResourcePath();
      registerAll(path, monitorImpl);
   }

   /**
    * Register the given directory with the WatchService
    */
   void unregister(ResourceMonitorImpl monitorImpl)
   {
      Set<Entry<WatchKey, ResourceMonitorImpl>> entrySet = keys.entrySet();
      Iterator<Entry<WatchKey, ResourceMonitorImpl>> iterator = entrySet.iterator();
      while (iterator.hasNext())
      {
         Entry<WatchKey, ResourceMonitorImpl> next = iterator.next();
         if (next.getValue() == monitorImpl)
         {
            next.getKey().cancel();
            iterator.remove();
         }
      }
   }

   /**
    * Register the given directory with the WatchService
    */
   private void register(Path path, ResourceMonitorImpl monitorImpl) throws IOException
   {
      WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
      keys.put(key, monitorImpl);
   }

   /**
    * Register the given directory, and all its sub-directories, with the WatchService.
    */
   private void registerAll(Path start, final ResourceMonitorImpl monitorImpl) throws IOException
   {
      // register directory and sub-directories
      Files.walkFileTree(start, new SimpleFileVisitor<Path>()
      {
         @Override
         public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                  throws IOException
         {
            register(dir, monitorImpl);
            return FileVisitResult.CONTINUE;
         }
      });
   }

   @SuppressWarnings("unchecked")
   @Override
   public void run()
   {
      while (alive)
      {
         WatchKey key;
         try
         {
            key = watcher.take();
         }
         catch (ClosedWatchServiceException | InterruptedException e)
         {
            break;
         }
         List<WatchEvent<?>> pollEvents = key.pollEvents();
         for (WatchEvent<?> event : pollEvents)
         {
            WatchEvent.Kind<?> kind = event.kind();
            if (kind == OVERFLOW)
            {
               continue;
            }

            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path name = ev.context();
            ResourceMonitorImpl resourceMonitor = keys.get(key);
            if (resourceMonitor == null)
            {
               if (log.isLoggable(Level.FINEST))
               {
                  log.finest("WatchKey not recognized " + name + " - " + key.watchable() + "> " + kind);
               }
               continue;
            }
            Path resourcePath = resourceMonitor.getResourcePath();
            Path child = resourcePath.resolve(name);
            if (log.isLoggable(Level.FINE))
            {
               log.log(Level.FINE, String.format("%s: %s %s %s\n", event.kind().name(), child, key, keys.keySet()));
            }
            if (kind == ENTRY_CREATE)
            {
               try
               {
                  if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS))
                  {
                     registerAll(child, resourceMonitor);
                  }
               }
               catch (IOException e)
               {
                  log.log(Level.SEVERE, "Error while registering child directories", e);
               }
               resourceMonitor.onPathCreate(child);
            }
            else if (kind == ENTRY_DELETE)
            {
               resourceMonitor.onPathDelete(child);
            }
            else if (kind == ENTRY_MODIFY)
            {
               resourceMonitor.onPathModify(child);
            }
         }

         if (!keys.containsKey(key))
         {
            // key is no longer available in the keys Map. Cancel it
            key.cancel();
         }
         else
         {
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid)
            {
               keys.remove(key);
            }
         }
      }
   }
}
