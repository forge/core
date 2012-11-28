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
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependency.collection.Dependencies;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
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
   private LinkedList<DependencyNode> requiredAddons = new LinkedList<DependencyNode>();

   public InstallRequest(AddonRepository repository, DependencyResolver resolver, DependencyNode requestedAddon)
   {
      this.repository = repository;
      this.requestedAddon = requestedAddon;
      this.dependencyResolver = resolver;
      calculateRequiredAddons();
   }

   public DependencyNode getRequestedAddon()
   {
      return this.requestedAddon;
   }

   /**
    * To return the required addons, we'll need to traverse the tree using the breadth first order, and then add them to
    * a stack. This will guarantee the order we need for the addons.
    *
    * @return
    */
   private void calculateRequiredAddons()
   {
      requiredAddons.clear();
      Iterator<DependencyNode> iterator = Dependencies.breadthFirstIterator(getRequestedAddon());
      while (iterator.hasNext())
      {
         DependencyNode node = iterator.next();
         if (Dependencies.isForgeAddon(node) && !node.equals(requestedAddon))
         {
            requiredAddons.push(node);
         }
      }
   }

   /**
    * Returns an unmodifiable list of the required addons
    *
    */
   public List<DependencyNode> getRequiredAddons()
   {
      return Collections.unmodifiableList(requiredAddons);
   }

   private AddonEntry toAddonEntry(DependencyNode dependencyNode)
   {
      Coordinate coord = dependencyNode.getDependency().getCoordinate();
      return AddonEntry.from(coord.getGroupId() + ":" + coord.getArtifactId(), coord.getVersion());
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

   private void deploy(AddonEntry entry, DependencyNode node)
   {
      Coordinate coordinate = node.getDependency().getCoordinate();
      File addonFile = dependencyResolver.resolveArtifact(DependencyQueryBuilder.create(coordinate));
      File[] addonDependencies = toDependencies(dependencyResolver.resolveAddonDependencies(coordinate
               .toString()));
      repository.deploy(entry, addonFile, addonDependencies);
   }

   private File[] toDependencies(List<Dependency> dependencies)
   {
      List<File> result = new ArrayList<File>();
      for (Dependency dependency : dependencies)
      {
         result.add(dependency.getArtifact());
      }
      return result.toArray(new File[dependencies.size()]);
   }

   private void enable(List<AddonEntry> entries)
   {
      for (AddonEntry addonEntry : entries)
      {
         repository.enable(addonEntry);
      }
   }
}
