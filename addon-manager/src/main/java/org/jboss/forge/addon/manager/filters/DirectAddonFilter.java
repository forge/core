package org.jboss.forge.addon.manager.filters;

import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.collection.Dependencies;
import org.jboss.forge.addon.dependency.collection.DependencyNodeFilter;

public class DirectAddonFilter implements DependencyNodeFilter
{
   private DependencyNode root;

   public DirectAddonFilter(DependencyNode root)
   {
      this.root = root;
   }

   @Override
   public boolean accept(DependencyNode node)
   {
      Coordinate artifact = node.getDependency().getCoordinate();
      if (Dependencies.isForgeAddon(artifact) && root.getChildren().contains(node))
      {
         return true;
      }
      return false;
   }
}