/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.facets.JTAFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * Implementation of {@link JTAFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class JTAFacetImpl extends AbstractJavaEEFacet implements JTAFacet
{
   private static final Dependency JBOSS_JTA_API = DependencyBuilder
            .create("org.jboss.spec.javax.transaction:jboss-transaction-api_1.1_spec");

   @Inject
   public JTAFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<Dependency, List<Dependency>>();
      result.put(JBOSS_JTA_API, Arrays.asList(JBOSS_JTA_API));
      return result;
   }
}
