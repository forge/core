/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.JTAFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@RunWith(Arquillian.class)
public class EEPluginTest extends AbstractShellTest
{
   @Test
   public void testSetupEJB() throws Exception
   {
      Project project = initializeJavaProject();

      assertFalse(project.hasFacet(EJBFacet.class));
      queueInputLines("");
      getShell().execute("setup ejb");
      assertTrue(project.hasFacet(EJBFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.ejb:jboss-ejb-api_3.1_spec")));
   }

   @Test
   public void testSetupJTA() throws Exception
   {
      Project project = initializeJavaProject();

      assertFalse(project.hasFacet(JTAFacet.class));
      queueInputLines("");
      getShell().execute("setup jta");
      assertTrue(project.hasFacet(JTAFacet.class));
   }
}
