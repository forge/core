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
import java.util.logging.Logger;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.filters.DirectAddonFilter;
import org.jboss.forge.addon.manager.impl.filters.LocalResourceFilter;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.DependencyNode;
import org.jboss.forge.dependencies.collection.Dependencies;
import org.jboss.forge.dependencies.collection.Predicate;

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
   private AddonRepository repository;

   private DependencyNode requestedAddonNode;
   private Stack<DependencyNode> requiredAddons = new Stack<DependencyNode>();
   private Stack<DependencyNode> optionalAddons = new Stack<DependencyNode>();

   private Logger log = Logger.getLogger(getClass().getName());

   /**
    * Package-access constructor. Only AddonManager should be allowed to call this constructor.
    * 
    * @param addonManager
    */
   InstallRequestImpl(AddonManager addonManager, AddonRepository repository, DependencyNode requestedAddonNode)
   {
      this.addonManager = addonManager;
      this.repository = repository;
      this.requestedAddonNode = requestedAddonNode;

      /*
       * To return the addons on which this addon depends, we'll need to traverse the tree using the breadth first
       * order, and then add them to a stack. This will guarantee their order.
       */
      Iterator<DependencyNode> iterator = Dependencies.breadthFirstIterator(requestedAddonNode);
      while (iterator.hasNext())
      {
         DependencyNode node = iterator.next();
         if (Dependencies.isForgeAddon(node.getDependency().getCoordinate()) && !node.equals(requestedAddonNode))
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

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.manager.impl.InstallRequest#getRequestedAddon()
    */
   @Override
   public DependencyNode getRequestedAddon()
   {
      return this.requestedAddonNode;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.manager.impl.InstallRequest#getOptionalAddons()
    */
   @Override
   public List<DependencyNode> getOptionalAddons()
   {
      return Collections.unmodifiableList(optionalAddons);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.manager.impl.InstallRequest#getRequiredAddons()
    */
   @Override
   public List<DependencyNode> getRequiredAddons()
   {
      return Collections.unmodifiableList(requiredAddons);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.manager.impl.InstallRequest#perform()
    */
   @Override
   public void perform()
   {
      for (DependencyNode requiredAddon : getRequiredAddons())
      {
         AddonId requiredAddonId = toAddonId(requiredAddon);
         if (repository.isDeployed(requiredAddonId))
         {
            log.info("Addon " + requiredAddonId + " is already deployed. Skipping...");
         }
         else
         {
            addonManager.install(requiredAddonId).perform();
         }
      }

      AddonId requestedAddonId = toAddonId(requestedAddonNode);
      deploy(requestedAddonId, requestedAddonNode);
      repository.enable(requestedAddonId);
   }

   private AddonId toAddonId(DependencyNode node)
   {
      Coordinate coord = node.getDependency().getCoordinate();
      DependencyNode forgeApi = Dependencies.selectFirst(Dependencies.breadthFirstIterator(node),
               new Predicate<DependencyNode>()
               {
                  @Override
                  public boolean accept(DependencyNode node)
                  {
                     Coordinate coordinate = node.getDependency().getCoordinate();
                     return "org.jboss.forge".equals(coordinate.getGroupId())
                              && "forge-addon-container-api".equals(coordinate.getArtifactId());
                  }
               });

      String apiVersion = null;
      if (forgeApi != null)
      {
         apiVersion = forgeApi.getDependency().getCoordinate().getVersion();
      }

      return AddonId.from(coord.getGroupId() + ":" + coord.getArtifactId(), coord.getVersion(), apiVersion);
   }

   private void deploy(AddonId addon, DependencyNode root)
   {
      List<File> resourceJars = toResourceJars(Dependencies.select(root, new LocalResourceFilter(root)));

      if (resourceJars.isEmpty())
      {
         log.fine("No resource JARs found for " + addon);
      }
      List<AddonDependency> addonDependencies =
               toAddonDependencies(Dependencies.select(root.getChildren().iterator(), new DirectAddonFilter(root)));

      if (addonDependencies.isEmpty())
      {
         log.fine("No dependencies found for addon " + addon);
      }
      log.info("Deploying addon " + addon);
      repository.deploy(addon, addonDependencies, resourceJars);
   }

   private List<AddonDependency> toAddonDependencies(List<DependencyNode> dependencies)
   {
      List<AddonDependency> addonDependencies = new ArrayList<AddonDependency>();
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
         AddonDependency addonDep = AddonDependency.create(toAddonId(dep), export, optional);
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
      return Dependencies.prettyPrint(requestedAddonNode, new Predicate<DependencyNode>()
      {
         @Override
         public boolean accept(DependencyNode node)
         {
            return Dependencies.isForgeAddon(node.getDependency().getCoordinate())
                     && !node.getDependency().isOptional();
         }
      });
   }
}
