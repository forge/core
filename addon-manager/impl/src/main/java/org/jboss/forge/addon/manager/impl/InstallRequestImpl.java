/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.collection.DependencyNodeUtil;
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.filters.DirectAddonFilter;
import org.jboss.forge.addon.manager.impl.filters.LocalResourceFilter;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Predicate;

/**
 * When an addon is installed, another addons could be required. This object returns the necessary information for the
 * installation of an addon to succeed, like required addons and dependencies
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class InstallRequestImpl implements InstallRequest
{
   private AddonManager addonManager;
   private Furnace forge;

   private DependencyNode requestedAddonNode;
   private Stack<DependencyNode> requiredAddons = new Stack<DependencyNode>();
   private Stack<DependencyNode> optionalAddons = new Stack<DependencyNode>();

   private Logger log = Logger.getLogger(getClass().getName());

   /**
    * Package-access constructor. Only AddonManager should be allowed to call this constructor.
    * 
    * @param addonManager
    */
   InstallRequestImpl(AddonManager addonManager, Furnace forge, DependencyNode requestedAddonNode)
   {
      this.addonManager = addonManager;
      this.forge = forge;
      this.requestedAddonNode = requestedAddonNode;

      /*
       * To return the addons on which this addon depends, we'll need to traverse the tree using the breadth first
       * order, and then add them to a stack. This will guarantee their order.
       */
      Iterator<DependencyNode> iterator = DependencyNodeUtil.breadthFirstIterator(requestedAddonNode);
      while (iterator.hasNext())
      {
         DependencyNode node = iterator.next();
         if (DependencyNodeUtil.isForgeAddon(node.getDependency().getCoordinate()) && !node.equals(requestedAddonNode))
         {
            if (node.getDependency().isOptional())
            {
               optionalAddons.push(node);
            }
            else
            {
               requiredAddons.push(node);
            }
         }
      }
   }

   @Override
   public DependencyNode getRequestedAddon()
   {
      return this.requestedAddonNode;
   }

   @Override
   public List<DependencyNode> getOptionalAddons()
   {
      return Collections.unmodifiableList(optionalAddons);
   }

   @Override
   public List<DependencyNode> getRequiredAddons()
   {
      return Collections.unmodifiableList(requiredAddons);
   }

   @Override
   public void perform()
   {
      forge.getLockManager().performLocked(LockMode.WRITE, new Callable<AddonId>()
      {
         @Override
         public AddonId call() throws Exception
         {
            for (DependencyNode requiredAddon : getRequiredAddons())
            {
               AddonId requiredAddonId = toAddonId(requiredAddon);
               boolean deployed = false;
               for (AddonRepository repository : forge.getRepositories())
               {
                  if (repository.isDeployed(requiredAddonId))
                  {
                     log.info("Addon " + requiredAddonId + " is already deployed. Skipping...");
                     deployed = true;
                     break;
                  }
               }

               if (!deployed)
               {
                  addonManager.install(requiredAddonId).perform();
               }
            }

            AddonId requestedAddonId = toAddonId(requestedAddonNode);

            for (AddonRepository repository : forge.getRepositories())
            {
               if (repository instanceof MutableAddonRepository)
               {
                  MutableAddonRepository mutableRepository = (MutableAddonRepository) repository;
                  deploy(mutableRepository, requestedAddonId, requestedAddonNode);
                  mutableRepository.enable(requestedAddonId);
                  break;
               }
            }
            return requestedAddonId;
         }
      });
   }

   @Override
   public void perform(final AddonRepository target)
   {
      Assert.isTrue(target instanceof MutableAddonRepository, "Addon repository ["
               + target.getRootDirectory().getAbsolutePath()
               + "] is not writable.");

      forge.getLockManager().performLocked(LockMode.WRITE, new Callable<AddonId>()
      {
         @Override
         public AddonId call() throws Exception
         {
            for (DependencyNode requiredAddon : getRequiredAddons())
            {
               AddonId requiredAddonId = toAddonId(requiredAddon);
               boolean deployed = false;
               for (AddonRepository repository : forge.getRepositories())
               {
                  if (repository.isDeployed(requiredAddonId))
                  {
                     log.info("Addon " + requiredAddonId + " is already deployed. Skipping...");
                     deployed = true;
                     break;
                  }
               }

               if (!deployed)
               {
                  addonManager.install(requiredAddonId).perform(target);
               }
            }

            AddonId requestedAddonId = toAddonId(requestedAddonNode);

            MutableAddonRepository mutableRepository = (MutableAddonRepository) target;
            deploy(mutableRepository, requestedAddonId, requestedAddonNode);
            mutableRepository.enable(requestedAddonId);
            return requestedAddonId;
         }
      });
   }

   private AddonId toAddonId(DependencyNode node)
   {
      Coordinate coord = node.getDependency().getCoordinate();
      DependencyNode forgeApi = DependencyNodeUtil.selectFirst(DependencyNodeUtil.breadthFirstIterator(node),
               new Predicate<DependencyNode>()
               {
                  @Override
                  public boolean accept(DependencyNode node)
                  {
                     Coordinate coordinate = node.getDependency().getCoordinate();
                     return "org.jboss.forge.furnace".equals(coordinate.getGroupId())
                              && "furnace-api".equals(coordinate.getArtifactId());
                  }
               });

      String apiVersion = null;
      if (forgeApi != null)
      {
         apiVersion = forgeApi.getDependency().getCoordinate().getVersion();
      }

      return AddonId.from(coord.getGroupId() + ":" + coord.getArtifactId(), coord.getVersion(), apiVersion);
   }

   private void deploy(MutableAddonRepository repository, AddonId addon, DependencyNode root)
   {
      List<File> resourceJars = toResourceJars(DependencyNodeUtil.select(root, new LocalResourceFilter(root)));

      if (resourceJars.isEmpty())
      {
         log.fine("No resource JARs found for " + addon);
      }
      List<AddonDependencyEntry> addonDependencies =
               toAddonDependencies(DependencyNodeUtil
                        .select(root.getChildren().iterator(), new DirectAddonFilter(root)));

      if (addonDependencies.isEmpty())
      {
         log.fine("No dependencies found for addon " + addon);
      }
      log.info("Deploying addon " + addon);
      repository.deploy(addon, addonDependencies, resourceJars);
   }

   private List<AddonDependencyEntry> toAddonDependencies(List<DependencyNode> dependencies)
   {
      List<AddonDependencyEntry> addonDependencies = new ArrayList<AddonDependencyEntry>();
      for (DependencyNode dep : dependencies)
      {
         boolean export = false;
         boolean optional = dep.getDependency().isOptional();
         String scopeType = dep.getDependency().getScopeType();
         if (scopeType != null && !optional)
         {
            if ("compile".equalsIgnoreCase(scopeType) || "runtime".equalsIgnoreCase(scopeType))
               export = true;
            else if ("provided".equalsIgnoreCase(scopeType))
               export = false;
         }
         AddonDependencyEntry addonDep = AddonDependencyEntry.create(toAddonId(dep).getName(), toAddonId(dep)
                  .getVersion().toString(), export, optional);
         addonDependencies.add(addonDep);
      }
      return addonDependencies;
   }

   private List<File> toResourceJars(List<DependencyNode> dependencies)
   {
      List<File> result = new ArrayList<File>();
      for (DependencyNode dependency : dependencies)
      {
         result.add(dependency.getDependency().getArtifact().getUnderlyingResourceObject());
      }
      return result;
   }

   @Override
   public String toString()
   {
      return DependencyNodeUtil.prettyPrint(requestedAddonNode, new Predicate<DependencyNode>()
      {
         @Override
         public boolean accept(DependencyNode node)
         {
            return DependencyNodeUtil.isForgeAddon(node.getDependency().getCoordinate())
                     && !node.getDependency().isOptional();
         }
      });
   }
}
