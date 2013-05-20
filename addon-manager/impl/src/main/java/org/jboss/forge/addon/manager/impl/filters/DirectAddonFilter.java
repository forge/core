package org.jboss.forge.addon.manager.impl.filters;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.collection.DependencyNodeUtil;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Checks if an addon node is a direct dependency of another node (root)
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
      if (DependencyNodeUtil.isForgeAddon(artifact) && root.getChildren().contains(node))
      {
         return true;
      }
      return false;
   }
}