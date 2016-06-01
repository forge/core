/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Implementation for JAX-RS 2.0
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestFacetImpl_2_0 extends AbstractRestFacet implements RestFacet_2_0
{
   private static final Dependency JAX_RS_2_0 = DependencyBuilder
            .create("javax.ws.rs:javax.ws.rs-api:2.0").setScopeType("provided");

   @Inject
   public RestFacetImpl_2_0(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      result.put(JAX_RS_2_0, Arrays.asList(JAX_RS_2_0, JAVAEE7));
      return result;
   }

   @Override
   protected Iterable<Dependency> getRequiredManagedDependenciesFor(Dependency dependency)
   {
      return Collections.emptySet();
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("2.0");
   }
}