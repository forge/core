/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.spec.javaee.JSTLFacet;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class JSTLPluginTest extends SingletonAbstractShellTest
{
   @Test
   public void testInstall() throws Exception
   {
      Project project = initializeJavaProject();

      assertFalse(project.hasFacet(JSTLFacet.class));
      queueInputLines("", "");
      getShell().execute("setup jstl");
      assertTrue(project.hasFacet(JSTLFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(DependencyBuilder
               .create("org.jboss.spec.javax.servlet.jstl:jboss-jstl-api_1.2_spec")));
   }

}
