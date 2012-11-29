package org.jboss.forge.addon.manager.filters;

import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.collection.Dependencies;
import org.jboss.forge.addon.dependency.collection.Predicate;

public class LocalResourceFilter implements Predicate<DependencyNode>
{
   @Override
   public boolean accept(DependencyNode node)
   {
      if (isDependencyAddon(node) || isProvided(node) || isNotLocal(node))
      {
         return false;
      }
      return true;
   }

   public boolean isDependencyAddon(DependencyNode node)
   {
      return Dependencies.isForgeAddon(node.getDependency().getCoordinate())
               && node.getParent() != null;
   }

   public boolean isProvided(DependencyNode node)
   {
      return "provided".equals(node.getDependency().getScopeType());
   }

   private boolean isNotLocal(DependencyNode node)
   {
      DependencyNode parent = node.getParent();
      while (parent != null)
      {
         if (isDependencyAddon(parent))
            return true;
         parent = parent.getParent();
      }
      return false;
   }
}