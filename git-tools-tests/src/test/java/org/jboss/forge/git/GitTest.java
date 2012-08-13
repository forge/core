/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.git;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;

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
