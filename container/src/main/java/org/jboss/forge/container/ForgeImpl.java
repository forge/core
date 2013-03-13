package org.jboss.forge.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.spi.ContainerLifecycleListener;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.modules.Module;
import org.jboss.modules.log.StreamModuleLogger;

public class ForgeImpl implements Forge
{
   private static Logger logger = Logger.getLogger(ForgeImpl.class.getName());

   private volatile boolean alive = false;
   private boolean serverMode = true;
   private AddonRepository repository = AddonRepositoryImpl.forDefaultDirectory();
   private AddonRegistryImpl registry = new AddonRegistryImpl(this);
   private List<ContainerLifecycleListener> registeredListeners = new ArrayList<ContainerLifecycleListener>();

   public ForgeImpl()
   {
      if (!AddonRepositoryImpl.hasRuntimeAPIVersion())
         logger.warning("Could not detect Forge runtime version - " +
                  "loading all addons, but failures may occur if versions are not compatible.");
   }

   public Forge enableLogging()
   {
      Module.setModuleLogger(new StreamModuleLogger(System.err));
      return this;
   }

   @Override
   public Forge startAsync()
   {
      return startAsync(Thread.currentThread().getContextClassLoader());
   }

   @Override
   public Forge startAsync(final ClassLoader loader)
   {
      new Thread()
      {
         @Override
         public void run()
         {
            ForgeImpl.this.start(loader);
         };
      }.start();

      return this;
   }

   @Override
   public Forge start()
   {
      return start(Thread.currentThread().getContextClassLoader());
   }

   @Override
   public Forge start(ClassLoader loader)
   {
      fireBeforeContainerStartedEvent(loader);
      if (!alive)
      {
         try
         {
            AddonModuleLoader moduleLoader = new AddonModuleLoader(repository, loader);
            registry.setAddonLoader(moduleLoader);
            alive = true;
            Set<Future<Addon>> futures = new HashSet<Future<Addon>>();
            do
            {
               futures.addAll(registry.startAll());
               Thread.sleep(100);
            }
            while (alive == true && (serverMode || isStartingAddons(futures)));
         }
         catch (InterruptedException e)
         {
            throw new ContainerException(e);
         }
         finally
         {
            fireBeforeContainerStoppedEvent(loader);
            registry.stopAll();
         }
      }
      fireAfterContainerStoppedEvent(loader);
      return this;
   }

   private void fireBeforeContainerStartedEvent(ClassLoader loader)
   {
      for (ContainerLifecycleListener listener : registeredListeners)
      {
         listener.beforeStart(this);
      }
      for (ContainerLifecycleListener listener : ServiceLoader.load(ContainerLifecycleListener.class, loader))
      {
         listener.beforeStart(this);
      }
   }

   private void fireBeforeContainerStoppedEvent(ClassLoader loader)
   {
      for (ContainerLifecycleListener listener : registeredListeners)
      {
         listener.beforeStop(this);
      }
      for (ContainerLifecycleListener listener : ServiceLoader.load(ContainerLifecycleListener.class, loader))
      {
         listener.beforeStop(this);
      }
   }

   private void fireAfterContainerStoppedEvent(ClassLoader loader)
   {
      for (ContainerLifecycleListener listener : registeredListeners)
      {
         listener.afterStop(this);
      }

      for (ContainerLifecycleListener listener : ServiceLoader.load(ContainerLifecycleListener.class, loader))
      {
         listener.afterStop(this);
      }

   }

   private boolean isStartingAddons(Set<Future<Addon>> futures)
   {
      for (Future<Addon> future : futures)
      {
         try
         {
            future.get(0, TimeUnit.MILLISECONDS);
         }
         catch (TimeoutException e)
         {
            return true;
         }
         catch (Exception e)
         {
            throw new ContainerException(e);
         }
      }
      return false;
   }

   @Override
   public Forge stop()
   {
      alive = false;
      return this;
   }

   @Override
   public Forge setAddonDir(File dir)
   {
      this.repository = AddonRepositoryImpl.forDirectory(dir);
      return this;
   }

   @Override
   public Forge setServerMode(boolean server)
   {
      this.serverMode = server;
      return this;
   }

   @Override
   public File getAddonDir()
   {
      return repository.getRepositoryDirectory();
   }

   @Override
   public AddonRegistry getAddonRegistry()
   {
      return registry;
   }

   @Override
   public AddonRepository getRepository()
   {
      return repository;
   }

   @Override
   public String getVersion()
   {
      return AddonRepositoryImpl.getRuntimeAPIVersion();
   }

   @Override
   public ListenerRegistration<ContainerLifecycleListener> addContainerLifecycleListener(
            final ContainerLifecycleListener listener)
   {
      registeredListeners.add(listener);
      return new ListenerRegistration<ContainerLifecycleListener>()
      {
         @Override
         public ContainerLifecycleListener removeListener()
         {
            registeredListeners.remove(listener);
            return listener;
         }
      };
   }
}
