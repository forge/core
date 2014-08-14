/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.facets.JavaEE6Facet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Implementation for the {@link JavaEE6Facet}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaEE6FacetImpl extends AbstractJavaEEFacet implements JavaEE6Facet
{

   @Inject
   public JavaEE6FacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return new SingleVersion("6");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      Dependency JAVAEE6_PROVIDED = DependencyBuilder.create(JAVAEE6).setScopeType("provided");
      result.put(JAVAEE6_PROVIDED, Arrays.asList(JAVAEE6_PROVIDED));
      return result;
   }

}
