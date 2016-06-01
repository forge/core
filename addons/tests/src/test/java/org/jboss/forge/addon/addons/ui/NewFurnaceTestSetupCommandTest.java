/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.ui;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewFurnaceTestSetupCommandTest
{
   private ProjectFactory projectFactory;
   private UITestHarness testHarness;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCreateTestClass() throws Exception
   {
      Project project = projectFactory.createTempProject();

      CommandController controller = testHarness.createCommandController(NewFurnaceTestSetupCommand.class,
               project.getRoot());
      controller.initialize();
      UISelectMany<AddonId> component = (UISelectMany<AddonId>) controller.getInputs().get("addonDependencies");
      controller.setValueFor("addonDependencies", component.getValueChoices());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertFalse(result instanceof Failed);

      Assert.assertTrue(project.hasFacet(AddonTestFacet.class));
   }
}
