package org.jboss.forge.addon.manager.impl.filters;

import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.DependencyNode;
import org.jboss.forge.dependencies.collection.Dependencies;

/**
 * Checks if an addon node is a direct dependency of another node (root)
 */
public class DirectAddonFilter implements Predicate<DependencyNode>
{
   private final DependencyNode root;

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