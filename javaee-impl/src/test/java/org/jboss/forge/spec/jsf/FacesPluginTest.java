/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jsf;

import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class FacesPluginTest extends AbstractShellTest
{

   @Test
   public void testFacesConfig() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("Y", "", "Y", "Y");
      getShell().execute("faces setup");
      Assert.assertTrue(project.getProjectRoot().getChild("src/main/webapp/WEB-INF/web.xml").exists());
   }

}