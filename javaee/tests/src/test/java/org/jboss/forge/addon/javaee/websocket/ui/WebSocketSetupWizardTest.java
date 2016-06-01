/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket.ui;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebSocketSetupWizardTest
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private UITestHarness uiTestHarness;

   @Test
   public void testSetupWebSockets() throws Exception
   {
      final Project project = projectFactory.createTempProject();
      try (CommandController tester = uiTestHarness
               .createCommandController(WebSocketSetupWizard.class, project.getRoot()))
      {
         // Launch
         tester.initialize();

         Assert.assertTrue(tester.isValid());
         Result result = tester.execute();
         Assert.assertEquals("WebSocket API 1.0 has been installed.", result.getMessage());
      }
   }
}
