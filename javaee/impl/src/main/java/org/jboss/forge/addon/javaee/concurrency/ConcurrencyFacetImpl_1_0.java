/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.concurrency;

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
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ConcurrencyFacetImpl_1_0 extends AbstractConcurrencyFacet
{
   private static final Dependency CONCURRENCY_API = DependencyBuilder
            .create("org.jboss.spec.javax.enterprise.concurrent:jboss-concurrency-api_1.0_spec:1.0.0.Final")
            .setScopeType("provided");

   @Inject
   public ConcurrencyFacetImpl_1_0(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("1.0");
   }

   @Override
   protected Iterable<Dependency> getRequiredManagedDependenciesFor(Dependency dependency)
   {
      return Collections.singleton(JAVAEE7);
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      result.put(CONCURRENCY_API, Arrays.asList(CONCURRENCY_API, JAVAEE7));
      return result;
   }
}
