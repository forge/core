/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.spec.javaee.PersistenceMetaModelFacet;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceMetaModelFacetTest extends AbstractJPATest
{

   @Test
   public void testInstallFacet() throws Exception
   {
      // when
      queueInputLines("", "");
      getShell().execute("project install-facet forge.spec.jpa.metamodel");
      
      // then
      MavenPluginFacet facet = getProject().getFacet(MavenPluginFacet.class);
      MavenPlugin processorPlugin = facet.getPlugin(DependencyBuilder.create("org.bsc.maven:maven-processor-plugin"));
      MavenPlugin compilerPlugin = facet.getPlugin(DependencyBuilder.create("org.apache.maven.plugins:maven-compiler-plugin"));
      
      assertTrue(getProject().hasFacet(PersistenceMetaModelFacet.class));
      assertNotNull(processorPlugin);
      assertEquals(1, processorPlugin.listExecutions().size());
      Configuration processorConfig = processorPlugin.listExecutions().get(0).getConfig();
      assertEquals("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor", 
               processorConfig.getConfigurationElement("processors").getChildByName("processor").getText());
      Configuration compilerConfig = compilerPlugin.getConfig();
      assertEquals("none", compilerConfig.getConfigurationElement("proc").getText());
   }

}
