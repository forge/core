/*

 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.facets.EJBFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EJBFacetImpl extends AbstractJavaEEFacet implements EJBFacet
{
   private static final Dependency JBOSS_EJB_API = DependencyBuilder
            .create("org.jboss.spec.javax.ejb:jboss-ejb-api_3.1_spec");
   private static final Dependency JAVAX_EJB_API = DependencyBuilder.create("javax.ejb:ejb-api");

   @Inject
   public EJBFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<Dependency, List<Dependency>>();

      result.put(JAVAX_EJB_API, Arrays.asList(JAVAX_EJB_API, JBOSS_EJB_API));

      return result;

   }

}
