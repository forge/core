/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import org.jboss.forge.furnace.addons.Addon;

/**
 * A range of versions to which an {@link Addon} may be restricted when building a dependency graph.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface VersionRange
{
   /**
    * Return <code>true</code> if this {@link VersionRange} is empty.
    */
   boolean isEmpty();

   /**
    * Return <code>true</code> if this {@link VersionRange} contains only one single {@link Version}.
    */
   boolean isExact();

   /**
    * Get the minimum {@link Version} (never <code>null</code>.)
    */
   Version getMin();

   /**
    * Get the maximum {@link Version} (never <code>null</code>.)
    */
   Version getMax();

   /**
    * Return true if this {@link VersionRange} includes the given {@link Version}.
    */
   boolean includes(Version version);

   /**
    * Return the intersection of this {@link VersionRange} with the given {@link VersionRange} instances (never
    * <code>null</code>.)
    */
   VersionRange getIntersection(VersionRange... ranges);
}
