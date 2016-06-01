/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jaxws;

import java.util.Arrays;
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
 * Implementation of {@link JAXWSFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class JAXWSFacetImpl extends AbstractJavaEEFacet implements JAXWSFacet
{
   private static final Dependency JBOSS_JAXB_API = DependencyBuilder
            .create("org.jboss.spec.javax.xml.bind:jboss-jaxb-api_2.2_spec");

   private static final Dependency JBOSS_JAXRPC_API = DependencyBuilder
            .create("org.jboss.spec.javax.xml.rpc:jboss-jaxrpc-api_1.1_spec");

   private static final Dependency JBOSS_SAAJ_API = DependencyBuilder
            .create("org.jboss.spec.javax.xml.soap:jboss-saaj-api_1.3_spec");

   private static final Dependency JBOSS_JAXWS_API = DependencyBuilder
            .create("org.jboss.spec.javax.xml.ws:jboss-jaxws-api_2.2_spec");

   @Inject
   public JAXWSFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "JAX-WS";
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("2.2");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<>();
      result.put(JBOSS_JAXB_API, Arrays.asList(JBOSS_JAXB_API, JAVAEE6, JAVAEE7));
      result.put(JBOSS_JAXRPC_API, Arrays.asList(JBOSS_JAXRPC_API, JAVAEE6, JAVAEE7));
      result.put(JBOSS_SAAJ_API, Arrays.asList(JBOSS_SAAJ_API, JAVAEE6, JAVAEE7));
      result.put(JBOSS_JAXWS_API, Arrays.asList(JBOSS_JAXWS_API, JAVAEE6, JAVAEE7));
      return result;
   }
}
