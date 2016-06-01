/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jms;

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
 * Implementation of {@link JMSFacet_2_0}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class JMSFacetImpl_2_0 extends AbstractJavaEEFacet implements JMSFacet_2_0
{
   private static final Dependency JMS_API = DependencyBuilder
            .create("javax.jms:javax.jms-api:2.0.1");

   @Inject
   public JMSFacetImpl_2_0(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "JMS";
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("2.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<>();
      result.put(JMS_API, Arrays.asList(JMS_API, JAVAEE7));
      return result;
   }
}
