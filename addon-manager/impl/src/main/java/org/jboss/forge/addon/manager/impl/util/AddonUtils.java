/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.collection.DependencyNodeUtil;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Utilities to
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public final class AddonUtils
{
   /**
    * Generates an {@link AddonId} based on a {@link DependencyNode} object
    * 
    * @param node
    * @return
    */
   public static AddonId from(DependencyNode node)
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

   public static List<AddonDependencyEntry> toAddonDependencies(List<DependencyNode> dependencies)
   {
      List<AddonDependencyEntry> addonDependencies = new ArrayList<AddonDependencyEntry>();
      for (DependencyNode dep : dependencies)
      {
         AddonDependencyEntry addonDep = toDependencyEntry(dep);
         addonDependencies.add(addonDep);
      }
      return addonDependencies;
   }

   public static AddonDependencyEntry toDependencyEntry(DependencyNode dep)
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
      AddonId from = AddonUtils.from(dep);
      AddonDependencyEntry addonDep = AddonDependencyEntry.create(from.getName(), from.getVersion().toString(),
               export,
               optional);
      return addonDep;
   }

   public static List<File> toResourceJars(List<DependencyNode> dependencies)
   {
      List<File> result = new ArrayList<File>();
      for (DependencyNode dependency : dependencies)
      {
         result.add(dependency.getDependency().getArtifact().getUnderlyingResourceObject());
      }
      return result;
   }

   public static Coordinate toDependencyCoordinate(AddonId addonId)
   {
      return CoordinateBuilder.create(addonId.getName()).setPackaging("jar")
               .setVersion(addonId.getVersion().toString())
               .setClassifier(DependencyNodeUtil.FORGE_ADDON_CLASSIFIER);
   }

}
