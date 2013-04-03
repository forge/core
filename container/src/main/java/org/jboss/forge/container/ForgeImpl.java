package org.jboss.forge.container;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.repositories.ImmutableAddonRepository;
import org.jboss.forge.container.spi.ContainerLifecycleListener;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.versions.SingleVersion;
import org.jboss.forge.container.versions.Version;
import org.jboss.modules.Module;
import org.jboss.modules.log.StreamModuleLogger;

public class ForgeImpl implements Forge
{
   private static Logger logger = Logger.getLogger(ForgeImpl.class.getName());

   private volatile boolean alive = false;
   private volatile ContainerStatus status = ContainerStatus.STOPPED;

   private boolean serverMode = true;
   private AddonRegistryImpl registry;
   private List<ContainerLifecycleListener> registeredListeners = new ArrayList<ContainerLifecycleListener>();

   private ClassLoader loader;

   private List<AddonRepository> repositories = new ArrayList<AddonRepository>();
   private Date lastCheckCompleted;

   private final LockManager lock = new LockManagerImpl();

   public ForgeImpl()
   {
      if (!AddonRepositoryImpl.hasRuntimeAPIVersion())
         logger.warning("Could not detect Forge runtime version - " +
                  "loading all addons, but failures may occur if versions are not compatible.");

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
            Thread.currentThread().setName("Forge Container " + ForgeImpl.this);
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
            do
            {
               boolean dirty = false;
               if (!isStartingAddons())
               {
                  Date nextCheck = new Date();
                  for (AddonRepository repository : repositories)
                  {
                     if (lastCheckCompleted == null || repository.isModifiedSince(lastCheckCompleted))
                     {
                        dirty = true;
                        for (Addon addon : registry.getAddons())
                        {
                           boolean enabled = false;
                           if (repository.isEnabled(addon.getId()))
                           {
                              enabled = true;
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
                        lastCheckCompleted = nextCheck;
                     }
                  }

                  if (dirty)
                  {
                     try
                     {
                        registry.startAll();
                     }
                     catch (Exception e)
                     {
                        logger.log(Level.SEVERE, "Error occurred.", e);
                     }
                  }
               }
               Thread.sleep(100);
            }
            while (alive && serverMode);

            while (alive && isStartingAddons())
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

   private boolean isStartingAddons()
   {
      return registry.isStartingAddons();
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
      status = ContainerStatus.STARTED;
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
      status = ContainerStatus.STOPPED;
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
      return Collections.unmodifiableList(repositories);
   }

   @Override
   public Forge addRepository(AddonRepositoryMode mode, File directory)
   {
      Assert.notNull(mode, "Addon repository mode must not be null.");
      Assert.notNull(mode, "Addon repository directory must not be null.");

      assertNotAlive();
      if (mode.isMutable())
         this.repositories.add(AddonRepositoryImpl.forDirectory(this, directory));
      else if (mode.isImmutable())
         this.repositories.add(new ImmutableAddonRepository(AddonRepositoryImpl.forDirectory(this, directory)));
      return this;
   }

   public void assertNotAlive()
   {
      if (alive)
         throw new IllegalStateException("Cannot modify a running Forge instance. Call .stop() first.");
   }

   @Override
   public ContainerStatus getStatus()
   {
      return isStartingAddons() ? ContainerStatus.STARTING : status;
   }
}
