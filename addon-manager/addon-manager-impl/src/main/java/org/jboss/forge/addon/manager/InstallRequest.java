/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.collection.Dependencies;
import org.jboss.forge.addon.dependency.collection.Predicate;
import org.jboss.forge.addon.manager.filters.DirectAddonFilter;
import org.jboss.forge.addon.manager.filters.LocalResourceFilter;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRepository;

/**
 * When an addon is installed, another addons could be required. This object returns the necessary information for the
 * installation of an addon to succeed, like required addons and dependencies
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class InstallRequest
{
   private AddonRepository repository;

   private DependencyNode requestedAddonNode;
   private Stack<DependencyNode> requiredAddons = new Stack<DependencyNode>();
   private Stack<DependencyNode> optionalAddons = new Stack<DependencyNode>();

   /**
    * Package-access constructor. Only AddonManager should be allowed to call this constructor.
    */
   InstallRequest(AddonRepository repository, DependencyNode requestedAddonNode)
   {
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

   public DependencyNode getRequestedAddon()
   {
      return this.requestedAddonNode;
   }

   /**
    * Returns an unmodifiable list of the required addons
    */
   public List<DependencyNode> getOptionalAddons()
   {
      return Collections.unmodifiableList(optionalAddons);
   }

   /**
    * Returns an unmodifiable list of the required addons
    */
   public List<DependencyNode> getRequiredAddons()
   {
      return Collections.unmodifiableList(requiredAddons);
   }

   /**
    * This will deploy all the required addons
    */
   public void perform()
   {
      List<AddonId> entries = new ArrayList<AddonId>(requiredAddons.size() + 1);

      // Deploy the required addons
      for (DependencyNode requiredAddon : getRequiredAddons())
      {
         AddonId addonEntry = toAddonId(requiredAddon);
         entries.add(addonEntry);
         deploy(addonEntry, requiredAddon);
      }

      // Deploy the requested addon
      AddonId requestedAddonId = toAddonId(requestedAddonNode);
      entries.add(requestedAddonId);
      deploy(requestedAddonId, requestedAddonNode);

      // Enable all the deployed addons
      enable(entries);
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

      List<AddonDependency> addonDependencies =
               toAddonDependencies(Dependencies.select(root, new DirectAddonFilter(root)));

      repository.deploy(addon, addonDependencies, resourceJars);
   }

   private List<AddonDependency> toAddonDependencies(List<DependencyNode> dependencies)
   {
      List<AddonDependency> addonDependencies = new ArrayList<AddonDependency>();
      for (DependencyNode dep : dependencies)
      {
         boolean export = false;
         boolean optional = dep.getDependency().isOptional();
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
         result.add(dependency.getDependency().getArtifact());
      }
      return result;
   }

   private void enable(List<AddonId> entries)
   {
      for (AddonId addonEntry : entries)
      {
         repository.enable(addonEntry);
      }
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
