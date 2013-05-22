package org.jboss.forge.furnace.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonDependency;
import org.jboss.forge.furnace.addons.AddonDependencyImpl;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonStatus;
import org.jboss.forge.furnace.addons.AddonTree;
import org.jboss.forge.furnace.addons.MarkLoadedAddonsDirtyVisitor;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.modules.AddonModuleLoader;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.ValuedVisitor;
import org.jboss.forge.furnace.versions.SingleVersionRange;
import org.jboss.modules.Module;

public class AddonLoader
{
   private static final Logger logger = Logger.getLogger(AddonLoader.class.getName());

   private Furnace furnace;
   private AddonTree tree;
   private LockManager lock;

   public AddonLoader(Furnace furnace, AddonTree tree)
   {
      this.furnace = furnace;
      this.tree = tree;
      this.lock = furnace.getLockManager();
   }

   public AddonImpl loadAddon(AddonId addonId)
   {
      Assert.notNull(addonId, "AddonId to load must not be null.");

      AddonImpl addon = null;
      for (Addon existing : tree)
      {
         if (existing.getId().equals(addonId))
         {
            addon = (AddonImpl) existing;
            break;
         }
      }

      if (addon == null)
      {
         for (AddonRepository repository : furnace.getRepositories())
         {
            addon = loadAddonFromRepository(repository, addonId);
            if (addon != null)
               break;
         }
      }
      else if (addon.getStatus().isMissing())
      {
         for (AddonRepository repository : furnace.getRepositories())
         {
            Addon loaded = loadAddonFromRepository(repository, addonId);
            if (loaded != null && !loaded.getStatus().isMissing())
               break;
         }
      }

      if (addon == null)
      {
         addon = new AddonImpl(lock, addonId);
         tree.add(addon);
      }

      return addon;
   }

   private AddonImpl loadAddonFromRepository(AddonRepository repository, final AddonId addonId)
   {
      AddonImpl addon = null;
      if (repository.isEnabled(addonId) && repository.isDeployed(addonId))
      {
         ValuedVisitor<AddonImpl, Addon> visitor = new ValuedVisitor<AddonImpl, Addon>()
         {
            @Override
            public void visit(Addon instance)
            {
               if (instance.getId().equals(addonId))
               {
                  setResult((AddonImpl) instance);
               }
            }
         };

         tree.depthFirst(visitor);

         addon = visitor.getResult();

         if (addon == null)
         {
            addon = new AddonImpl(lock, addonId);
            addon.setRepository(repository);
            tree.add(addon);
         }

         Set<AddonDependency> dependencies = fromAddonDependencyEntries(addon,
                  repository.getAddonDependencies(addonId));
         addon.setDependencies(dependencies);
         tree.prune();

         if (addon.getModule() == null)
         {
            Set<AddonDependency> missingRequiredDependencies = new HashSet<AddonDependency>();
            for (AddonDependency dependency : addon.getDependencies())
            {
               AddonId dependencyId = dependency.getDependency().getId();

               boolean loaded = false;
               for (Addon a : tree)
               {
                  if (a.getId().equals(dependencyId) && !a.getStatus().isMissing())
                  {
                     loaded = true;
                  }
               }
               if (!loaded && !dependency.isOptional())
               {
                  missingRequiredDependencies.add(dependency);
               }
            }

            if (!missingRequiredDependencies.isEmpty())
            {
               if (addon.getMissingDependencies().size() != missingRequiredDependencies.size())
               {
                  logger.warning("Addon [" + addon + "] has [" + missingRequiredDependencies.size()
                           + "] missing dependencies: "
                           + missingRequiredDependencies + " and will be not be loaded until all required"
                           + " dependencies are available.");
               }
               addon.setMissingDependencies(missingRequiredDependencies);
            }
            else
            {
               try
               {
                  AddonModuleLoader moduleLoader = getAddonModuleLoader(repository);
                  Module module = moduleLoader.loadModule(addonId);
                  addon.setModuleLoader(moduleLoader);
                  addon.setModule(module);
                  addon.setRepository(repository);
                  addon.setStatus(AddonStatus.LOADED);

                  tree.depthFirst(new MarkLoadedAddonsDirtyVisitor(tree, addon));

               }
               catch (Exception e)
               {
                  logger.log(Level.FINE, "Failed to load addon [" + addonId + "]", e);
                  // throw new ContainerException("Failed to load addon [" + addonId + "]", e);
               }
            }
         }
      }
      return addon;
   }

   private AddonModuleLoader loader;

   private AddonModuleLoader getAddonModuleLoader(AddonRepository repository)
   {
      Assert.notNull(repository, "Repository must not be null.");

      if (loader == null)
      {
         loader = new AddonModuleLoader(furnace);
      }
      return loader;
   }

   private Set<AddonDependency> fromAddonDependencyEntries(AddonImpl addon, Set<AddonDependencyEntry> entries)
   {
      Set<AddonDependency> result = new HashSet<AddonDependency>();
      for (AddonDependencyEntry entry : entries)
      {
         result.add(new AddonDependencyImpl(lock, addon, new SingleVersionRange(entry.getId().getVersion()),
                  loadAddon(entry.getId()), entry.isExported(), entry.isOptional()));
      }
      return result;
   }
}
