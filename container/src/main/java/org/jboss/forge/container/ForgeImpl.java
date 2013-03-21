package org.jboss.forge.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.spi.ContainerLifecycleListener;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.versions.SingleVersion;
import org.jboss.forge.container.versions.Version;
import org.jboss.modules.Module;
import org.jboss.modules.log.StreamModuleLogger;

public class ForgeImpl implements Forge
{
   private static Logger logger = Logger.getLogger(ForgeImpl.class.getName());

   private volatile boolean alive = false;
   private boolean serverMode = true;
   private AddonRegistryImpl registry;
   private List<ContainerLifecycleListener> registeredListeners = new ArrayList<ContainerLifecycleListener>();

   private ClassLoader loader;

   private List<AddonRepository> repositories = new ArrayList<AddonRepository>();

   private final LockManager lock = new LockManagerImpl();

   public ForgeImpl()
   {
      if (!AddonRepositoryImpl.hasRuntimeAPIVersion())
         logger.warning("Could not detect Forge runtime version - " +
                  "loading all addons, but failures may occur if versions are not compatible.");

      repositories.add(AddonRepositoryImpl.forDefaultDirectory(this));
      registry = new AddonRegistryImpl(this, lock);
   }

   @Override
   public LockManager getLockManager()
   {
      return lock;
   }

   @Override
   public ClassLoader getRuntimeClassLoader()
   {
      return loader;
   }

   public Forge enableLogging()
   {
      assertNotAlive();
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
      assertNotAlive();
      this.loader = loader;
      fireBeforeContainerStartedEvent(loader);
      if (!alive)
      {
         try
         {
            alive = true;
            Set<Future<Addon>> futures = new HashSet<Future<Addon>>();
            do
            {
               if (!isStartingAddons(futures))
               {
                  for (Addon addon : registry.getRegisteredAddons())
                  {
                     boolean enabled = false;
                     for (AddonRepository repository : repositories)
                     {
                        if (repository.isEnabled(addon.getId()))
                        {
                           enabled = true;
                           break;
                        }
                     }

                     if (!enabled && addon.getStatus().isStarted())
                     {
                        try
                        {
                           registry.stop(addon);
                        }
                        catch (Exception e)
                        {
                           logger.log(Level.SEVERE, "Error occurred.", e);
                        }
                     }
                  }

                  try
                  {
                     futures.addAll(registry.startAll());
                  }
                  catch (Exception e)
                  {
                     logger.log(Level.SEVERE, "Error occurred.", e);
                  }
               }
               Thread.sleep(100);
            }
            while (alive && serverMode);

            while (alive && isStartingAddons(futures))
            {
               Thread.sleep(100);
            }
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Error occurred.", e);
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
   public Forge setServerMode(boolean server)
   {
      assertNotAlive();
      this.serverMode = server;
      return this;
   }

   @Override
   public AddonRegistry getAddonRegistry()
   {
      return registry;
   }

   @Override
   public Version getVersion()
   {
      return new SingleVersion(AddonRepositoryImpl.getRuntimeAPIVersion());
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

   @Override
   public List<AddonRepository> getRepositories()
   {
      return repositories;
   }

   @Override
   public Forge setRepositories(AddonRepository... repositories)
   {
      List<AddonRepository> temp = new ArrayList<AddonRepository>();
      for (AddonRepository repository : repositories)
      {
         temp.add(repository);
      }
      return setRepositories(temp);
   }

   @Override
   public Forge setRepositories(List<AddonRepository> repositories)
   {
      assertNotAlive();
      this.repositories = repositories;
      return this;
   }

   public void assertNotAlive()
   {
      if (alive)
         throw new IllegalStateException("Cannot modify a running Forge instance. Call .stop() first.");
   }
}
