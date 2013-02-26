/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.services;

import static org.junit.Assert.assertNotNull;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ProjectFactoryTest extends AbstractShellTest
{
   @Test
   public void testCDintoProjectRegistersFacets() throws Exception
   {
      Shell shell = getShell();
      initializeJavaProject();

      Project project = getProject();
      Assert.assertNotNull(project);
      Resource<?> projectResource = shell.getCurrentResource();

      shell.execute("cd /");

      Resource<?> newResource = shell.getCurrentResource();
      Assert.assertNotSame(projectResource, newResource);

      shell.execute("cd -");
      Resource<?> currentResource = shell.getCurrentResource();

      Assert.assertEquals(projectResource, currentResource);

      project = getProject();
      Assert.assertNotNull(project.getProjectRoot());

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      assertNotNull(javaSourceFacet);
   }
}
