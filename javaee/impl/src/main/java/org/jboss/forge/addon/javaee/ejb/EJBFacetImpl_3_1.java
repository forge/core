/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb;

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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EJBFacetImpl_3_1 extends AbstractEJBFacetImpl implements EJBFacet_3_1
{
   private static final Dependency JBOSS_EJB_API = DependencyBuilder
            .create("org.jboss.spec.javax.ejb:jboss-ejb-api_3.1_spec").setScopeType("provided");
   private static final Dependency GLASSFISH_EJB_API = DependencyBuilder.create("org.glassfish:javax.ejb::3.1")
            .setScopeType("provided");

   @Inject
   public EJBFacetImpl_3_1(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("3.1");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<Dependency, List<Dependency>>();

      result.put(JBOSS_EJB_API, Arrays.asList(GLASSFISH_EJB_API, JBOSS_EJB_API));

      return result;

   }

}
