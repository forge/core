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
import org.jboss.forge.addon.dependency.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependency.collection.Dependencies;
import org.jboss.forge.addon.dependency.collection.DependencyNodeFilter;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.jboss.forge.addon.manager.filters.LocalResourceFilter;
import org.jboss.forge.addon.manager.filters.DirectAddonFilter;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonEntry;
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
   private DependencyResolver dependencyResolver;

   private DependencyNode requestedAddon;
   private Stack<DependencyNode> requiredAddons = new Stack<DependencyNode>();
   private Stack<DependencyNode> optionalAddons = new Stack<DependencyNode>();

   public InstallRequest(AddonRepository repository, DependencyResolver resolver, DependencyNode requestedAddon)
   {
      this.repository = repository;
      this.requestedAddon = requestedAddon;
      this.dependencyResolver = resolver;

      /*
       * To return the addons on which this addon depends, we'll need to traverse the tree using the breadth first
       * order, and then add them to a stack. This will guarantee their order.
       */
      requiredAddons.clear();
      Iterator<DependencyNode> iterator = Dependencies.breadthFirstIterator(requestedAddon);
      while (iterator.hasNext())
      {
         DependencyNode node = iterator.next();
         if (Dependencies.isForgeAddon(node) && !node.equals(requestedAddon))
         {
            if (!node.getDependency().isOptional())
               requiredAddons.push(node);
            else
               optionalAddons.push(node);
         }
      }
   }

   public DependencyNode getRequestedAddon()
   {
      return this.requestedAddon;
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
    * This will deploy all the required addons and
    */
   public void perform()
   {
      List<AddonEntry> entries = new ArrayList<AddonEntry>(requiredAddons.size() + 1);
      for (DependencyNode requiredAddon : getRequiredAddons())
      {
         AddonEntry addonEntry = toAddonEntry(requiredAddon);
         entries.add(addonEntry);
         deploy(addonEntry, requiredAddon);
      }

      AddonEntry requestedAddonEntry = toAddonEntry(requestedAddon);
      entries.add(requestedAddonEntry);
      deploy(requestedAddonEntry, requestedAddon);

      enable(entries);
   }

   private AddonEntry toAddonEntry(DependencyNode node)
   {
      Coordinate coord = node.getDependency().getCoordinate();
      List<DependencyNode> forgeApi = Dependencies.collect(Dependencies.breadthFirstIterator(node),
               new DependencyNodeFilter()
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
      if (forgeApi.size() > 0)
      {
         apiVersion = forgeApi.get(0).getDependency().getCoordinate().getVersion();
      }

      return AddonEntry.from(coord.getGroupId() + ":" + coord.getArtifactId(), coord.getVersion(), apiVersion);
   }

   private void deploy(AddonEntry addon, DependencyNode root)
   {
      Coordinate coordinate = root.getDependency().getCoordinate();

      dependencyResolver.resolveDependencyHierarchy(DependencyQueryBuilder.create(coordinate));
      List<File> resourceJars = toResourceJars(Dependencies.collect(root, new LocalResourceFilter()));
      resourceJars.add(dependencyResolver.resolveArtifact(DependencyQueryBuilder.create(coordinate)).getArtifact());

      List<AddonDependency> addonDependencies =
               toAddonDependencies(Dependencies.collect(root, new DirectAddonFilter(root)));

      repository.deploy(addon, addonDependencies, resourceJars);
   }

   private List<AddonDependency> toAddonDependencies(List<DependencyNode> dependencies)
   {
      return null;
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

   private void enable(List<AddonEntry> entries)
   {
      for (AddonEntry addonEntry : entries)
      {
         repository.enable(addonEntry);
      }
   }
}
