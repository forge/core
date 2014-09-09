/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.javaee.facets.JavaEE7Facet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Implementation for the {@link JavaEE7Facet}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaEE7FacetImpl extends AbstractJavaEEFacet implements JavaEE7Facet
{

   @Inject
   public JavaEE7FacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return new SingleVersion("7");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      result.put(JAVAEE7, Arrays.asList(JAVAEE7));
      return result;
   }

   @Override
   protected Iterable<Dependency> getRequiredManagedDependenciesFor(Dependency dependency)
   {
      return Collections.emptySet();
   }
}
