/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.events.CommandExecuted;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@RunWith(Arquillian.class)
public class CommandExecutedTest extends AbstractShellTest
{

   @Inject
   CommandExecutedObserver observer;

   @Inject
   @Alias("motp")
   MockOptionTestPlugin plugin;

   @Test
   public void testInvalidSuppliedOptionIsCorrected() throws Exception
   {
      getShell().execute("motp motp");
      CommandExecuted event = observer.getEvent();
      Assert.assertNotNull(observer.getPreCommandExecutionEvent());
      Assert.assertNotNull(event);
      Assert.assertEquals(CommandExecuted.Status.SUCCESS, event.getStatus());
      Assert.assertNotNull(plugin.getDefaultCommandArg());
      CommandMetadata command = event.getCommand();
      Assert.assertNotNull(command);
      Assert.assertEquals("motp", command.getName());
      Object[] parameters = event.getParameters();
      Assert.assertNotNull(parameters);
      Assert.assertEquals(1, parameters.length);
      Assert.assertEquals("motp", parameters[0]);
   }

   @Test
   public void testCommandIsVetoed() throws Exception
   {
      observer.setVeto(true);
      getShell().execute("motp vetoed");
      Assert.assertNotNull(observer.getPreCommandExecutionEvent());
      Assert.assertNull("CommandExecuted should not have been fired when Status == VETOED", observer.getEvent());
      Assert.assertNull("Default Command should not have been fired", plugin.getDefaultCommandArg());
   }

   @After
   public void resetObserverAndPluginState()
   {
      // Observer and plugin are singletons
      observer.reset();
      plugin.setDefaultCommandArg(null);
   }

}
