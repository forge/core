/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.jboss.forge.addon.dependencies.AddonDependencyResolver;
import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.collection.DependencyNodeUtil;
import org.jboss.forge.addon.manager.AddonInfo;
import org.jboss.forge.addon.manager.impl.filters.LocalResourceFilter;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.addons.AddonId;

/**
 * Makes {@link AddonInfo#getResources()} lazy
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
class LazyAddonInfo implements AddonInfo
{
   private final AddonDependencyResolver resolver;
   private AddonInfoBuilder builder;

   public LazyAddonInfo(AddonDependencyResolver resolver, AddonInfoBuilder builder)
   {
      this.resolver = resolver;
      this.builder = builder;
   }

   @Override
   public Set<File> getResources()
   {
      resolveResources(builder);
      return builder.getResources();
   }

   @Override
   public Set<AddonInfo> getRequiredAddons()
   {
      return builder.getRequiredAddons();
   }

   @Override
   public Set<AddonInfo> getOptionalAddons()
   {
      return builder.getOptionalAddons();
   }

   @Override
   public DependencyNode getDependencyNode()
   {
      return builder.getDependencyNode();
   }

   @Override
   public AddonId getAddon()
   {
      return builder.getAddon();
   }

   @Override
   public boolean equals(Object obj)
   {
      return builder.equals(obj);
   }

   @Override
   public int hashCode()
   {
      return builder.hashCode();
   }

   @Override
   public String toString()
   {
      return builder.toString();
   }

   public void resolveResources(AddonInfoBuilder addonInfo)
   {
      DependencyNode node = addonInfo.getDependencyNode();
      LocalResourceFilter filter = new LocalResourceFilter(node);
      Iterator<DependencyNode> it = DependencyNodeUtil.breadthFirstIterator(node);
      while (it.hasNext())
      {
         DependencyNode resourceNode = it.next();
         // Add resources
         if (filter.accept(resourceNode))
         {
            FileResource<?> artifact = resolver.resolveArtifact(
                     DependencyQueryBuilder.create(resourceNode.getDependency().getCoordinate())).getArtifact();
            addonInfo.addResource(artifact.getUnderlyingResourceObject());

         }
      }
   }
}