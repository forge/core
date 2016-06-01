/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jstl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.jta.JTAFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Implementation of {@link JTAFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class JSTLFacetImpl extends AbstractJavaEEFacet implements JSTLFacet
{
   private static final Dependency JBOSS_JSTL_API = DependencyBuilder
            .create("org.jboss.spec.javax.servlet.jstl:jboss-jstl-api_1.2_spec");

   @Inject
   public JSTLFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "JSTL";
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
      result.put(JBOSS_JSTL_API, Arrays.asList(JBOSS_JSTL_API));
      return result;
   }
}
