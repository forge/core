/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 *
 */
public class GitTest extends AbstractShellTest
{

   @Inject
   private DependencyResolver resolver;

   @Deployment
   public static JavaArchive getDeployment()
   {
      // The deployment method is where you must add references to your classes, packages, and
      // configuration files, via Arquillian.
      return AbstractShellTest.getDeployment().addPackages(true, Git.class.getPackage());
   }

   @Test
   public void testGitFacetSetup() throws Exception
   {
      // Create a new barebones Java project
      Project p = initializeJavaProject();

      // Execute a command. If any input is required, it will be read from queued input.
      getShell().execute("git setup");

      Assert.assertNotNull(resolver);
      Assert.assertTrue(p.getProjectRoot().getChildDirectory(".git").exists());
   }
}
