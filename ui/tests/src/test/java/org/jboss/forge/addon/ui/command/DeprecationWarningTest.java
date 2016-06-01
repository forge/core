/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.example.commands.DeprecatedByAnnotationCommand;
import org.jboss.forge.addon.ui.example.commands.DeprecatedCommand;
import org.jboss.forge.addon.ui.example.commands.DeprecatedInputCommand;
import org.jboss.forge.addon.ui.example.commands.SimpleCommand;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("deprecation")
@RunWith(Arquillian.class)
public class DeprecationWarningTest
{
   @Inject
   UITestHarness uiTestHarness;

   @Test
   public void testDeprecationWarning() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(DeprecatedCommand.class))
      {
         controller.initialize();
         List<UIMessage> messages = controller.validate();
         Assert.assertThat(messages.size(), equalTo(1));
         Assert.assertThat(messages.get(0).getSeverity(), equalTo(UIMessage.Severity.WARN));
         Assert.assertThat(messages.get(0).getDescription(), equalTo(
                  "The command 'deprecated-command' is deprecated and will be removed in future versions. Do not use this command anymore"));
      }
   }

   @Test
   public void testDeprecationByAnnotationWarning() throws Exception
   {
      try (CommandController controller = uiTestHarness
               .createCommandController(DeprecatedByAnnotationCommand.class))
      {
         controller.initialize();
         List<UIMessage> messages = controller.validate();
         Assert.assertThat(messages.size(), equalTo(1));
         Assert.assertThat(messages.get(0).getSeverity(), equalTo(UIMessage.Severity.WARN));
         Assert.assertThat(messages.get(0).getDescription(), equalTo(
                  "The command 'deprecated-by-annotation-command' is deprecated and will be removed in future versions."));
      }
   }

   @Test
   public void testNoDeprecationWarning() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(SimpleCommand.class))
      {
         controller.initialize();
         List<UIMessage> messages = controller.validate();
         Assert.assertThat(messages.size(), equalTo(0));
         Assert.assertThat(controller.isValid(), equalTo(true));
      }
   }

   @Test
   public void testNoInputDeprecationWarning() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(DeprecatedInputCommand.class))
      {
         controller.initialize();
         List<UIMessage> messages = controller.validate();
         Assert.assertThat(messages.size(), equalTo(0));
         Assert.assertThat(controller.isValid(), equalTo(true));
      }
   }

   @Test
   public void testInputDeprecationWarning() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(DeprecatedInputCommand.class))
      {
         controller.initialize();
         controller.setValueFor("deprecatedInput", "foo");
         List<UIMessage> messages = controller.validate();
         Assert.assertThat(messages.size(), equalTo(1));
         Assert.assertThat(messages.get(0).getSeverity(), equalTo(UIMessage.Severity.WARN));
         Assert.assertThat(messages.get(0).getDescription(), equalTo(
                  "The parameter 'deprecatedInput' from command 'deprecated-input' is deprecated and will be removed in future versions."));
      }
   }

}
