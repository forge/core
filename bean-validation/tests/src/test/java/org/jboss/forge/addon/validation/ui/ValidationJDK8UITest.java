/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.validation.ui;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ValidationJDK8UITest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:bean-validation"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClass(ValidationJDK8Command.class)
               .addClass(NotFoo.class)
               .addClass(NotFooValidator.class);

      return archive;
   }

   @Inject
   UITestHarness testHarness;

   @Test
   public void testValidation() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController(ValidationJDK8Command.class))
      {
         controller.initialize();
         controller.setValueFor("name", "Foo");
         Assert.assertFalse("Controller should not be valid", controller.isValid());
         List<UIMessage> messages = controller.validate();
         Assert.assertEquals("An error should have been captured", 1, messages.size());
         Assert.assertEquals("Name: My Error Message", messages.get(0).getDescription());
      }
   }
}
