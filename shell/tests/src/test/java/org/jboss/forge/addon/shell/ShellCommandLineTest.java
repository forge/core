/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.line.CommandLine;
import org.jboss.forge.addon.shell.line.CommandOption;
import org.jboss.forge.addon.shell.mock.command.Career;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ShellCommandLineTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")

   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClasses(FooCommand.class, Career.class);

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Before
   public void setUp() throws Exception
   {
      shellTest.clearScreen();
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test(timeout = 10000)
   public void testCommandLineShouldContainExecutionData() throws Exception
   {
      final AtomicReference<CommandLine> ref = new AtomicReference<>();
      shellTest.getShell().addCommandExecutionListener(new AbstractCommandExecutionListener()
      {
         @Override
         public void preCommandExecuted(UICommand command, UIExecutionContext context)
         {
            ShellContext uiContext = (ShellContext) context.getUIContext();
            ref.set(uiContext.getCommandLine());
         }
      });
      {
         shellTest.execute("foocommand --name George --help HALP", 10, TimeUnit.SECONDS);
         CommandLine commandLine = ref.get();
         Assert.assertThat(commandLine, notNullValue());
         Assert.assertThat(commandLine.hasParameters(), is(true));
         Assert.assertThat(commandLine.getArgument(), nullValue());
         List<CommandOption> options = commandLine.getOptions();
         Assert.assertThat(options.size(), equalTo(2));
         Assert.assertThat(options.get(0).getName(), equalTo("name"));
         Assert.assertThat(options.get(0).getValue(), equalTo("George"));
         Assert.assertThat(options.get(1).getName(), equalTo("help"));
         Assert.assertThat(options.get(1).getValue(), equalTo("HALP"));
      }
      {
         shellTest.execute("foocommand", 10, TimeUnit.SECONDS);
         CommandLine commandLine = ref.get();
         Assert.assertThat(commandLine, notNullValue());
         Assert.assertThat(commandLine.hasParameters(), is(false));
         Assert.assertThat(commandLine.getArgument(), nullValue());
         List<CommandOption> options = commandLine.getOptions();
         Assert.assertThat(options.size(), equalTo(0));
      }
   }
}
