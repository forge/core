package org.jboss.forge.addon.dependencies.util;


import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.container.util.Predicate;

/**
 * {@link Predicate} used to filter out SNAPSHOT {@link Dependency} instances.
 */
public class NonSnapshotDependencyFilter implements Predicate<Dependency>
{
   @Override
   public boolean accept(Dependency dependency)
   {
      return dependency != null && !dependency.getCoordinate().isSnapshot();
   }
}