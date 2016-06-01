/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class WebSocketFacetImpl_1_0 extends AbstractJavaEEFacet implements WebSocketFacet_1_0
{

   private static final Dependency JAVAX_WEBSOCKETS_API = DependencyBuilder.create(
            "javax.websocket:javax.websocket-api:1.0")
            .setScopeType("provided");

   @Inject
   public WebSocketFacetImpl_1_0(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "WebSocket";
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("1.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<Dependency, List<Dependency>>();
      result.put(JAVAX_WEBSOCKETS_API, Arrays.asList(JAVAX_WEBSOCKETS_API, JAVAEE7));
      return result;
   }

   @Override
   protected Iterable<Dependency> getRequiredManagedDependenciesFor(Dependency dependency)
   {
      return Collections.emptyList();
   }

}
