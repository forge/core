/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Implementation of {@link JTAFacet}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class JTAFacetImpl_1_2 extends AbstractJTAFacetImpl implements JTAFacet_1_2
{
   private static final Dependency JTA_API = DependencyBuilder
            .create("javax.transaction:javax.transaction-api:1.2").setScopeType("provided");

   @Inject
   public JTAFacetImpl_1_2(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("1.2");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<>();
      result.put(JTA_API, Arrays.asList(JTA_API, JAVAEE7));
      return result;
   }
}
