package org.jboss.forge.addon.manager.impl.filters;

import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.dependencies.DependencyNode;
import org.jboss.forge.dependencies.collection.Dependencies;

/**
 * Figures out if a dependency should be treated as a JAR or as an Addon.
 *
 * Also if that dependency is a direct dependency of the current addon.
 *
 * If it is direct, then it should be accepted and packaged as a JAR in the addon deployment. Otherwise, ignore it
 *
 */
public class LocalResourceFilter implements Predicate<DependencyNode>
{
   private final DependencyNode addon;

   public LocalResourceFilter(DependencyNode addon)
   {
      this.addon = addon;
   }

   @Override
   public boolean accept(DependencyNode node)
   {
      if (isDependencyAddon(node) || isProvided(node))
      {
         return false;
      }
      return true;
   }

   public boolean isDependencyAddon(DependencyNode node)
   {
      return (Dependencies.isForgeAddon(node.getDependency().getCoordinate()) && !node.equals(addon));
   }

   public boolean isProvided(DependencyNode node)
   {
      return "provided".equals(node.getDependency().getScopeType());
   }

}