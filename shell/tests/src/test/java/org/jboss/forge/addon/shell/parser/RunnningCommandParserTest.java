/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.ForgeShell;
import org.jboss.forge.addon.shell.ShellCommand;
import org.jboss.forge.addon.shell.TestWizard;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.shell.util.CommandLineUtil;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RunnningCommandParserTest
{
   private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:dependencies", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.furnace:container-cdi", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(TestWizard.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness",
                                 "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:dependencies", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace:container-cdi", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   @Inject
   private ForgeShell shell;

   @Inject
   private AddonRegistry registry;

   @Inject
   private ShellTest test;

   @Inject
   private TestWizard testWizard;

   @Test
   public void testWizardParse() throws Exception
   {
      ShellCommand command = new ShellCommand(null, shell, testWizard);
      String input = "test-project --named foo --type war --topLevelPackage org.foo --targetLocation /tmp";
      CommandLine cl = command.parse(input);

      assertEquals("foo", cl.getOptionValue("named"));
      assertEquals("war", cl.getOptionValue("type"));
      assertEquals("org.foo", cl.getOptionValue("topLevelPackage"));
      assertEquals("/tmp", cl.getOptionValue("targetLocation"));

      // ConsoleOutput output = new ConsoleOutput(new ConsoleOperation(ControlOperator.NONE, input));
      // Result result = command.run(output, cl);

   }

   @Test
   public void testWizardComplete() throws Exception
   {
      ShellCommand command = new ShellCommand(null, shell, testWizard);
      CompleteOperation complete = new CompleteOperation("test-project --named foo --targetLocation /tm", 20);
      command.complete(complete);
      if (Config.isOSPOSIXCompatible())
         assertEquals("p", complete.getCompletionCandidates().get(0));

      complete = new CompleteOperation("test-project --named foo --type ", 20);
      command.complete(complete);

      System.out.println("after complete: " + complete.toString());

   }

   @Test
   public void testPopulateUIInputs() throws Exception
   {
      ShellCommand command = new ShellCommand(null, shell, testWizard);
      String input = "test-project --named foo --type war --topLevelPackage org.foo --targetLocation /tmp";
      CommandLine cl = command.parse(input);

      CommandLineUtil.populateUIInputs(cl, command.getContext(), registry);

      assertEquals("foo", ((UIInput) command.getContext().findInput("named")).getValue());
      // this doesnt work yet
      // assertEquals("war", ((UISelectOne) command.getContext().findInput("type")).getValue());

   }

}
