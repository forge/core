/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.events.CommandExecuted;
import org.jboss.forge.test.AbstractShellTest;
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

   @Test
   public void testInvalidSuppliedOptionIsCorrected() throws Exception
   {
      getShell().execute("motp motp");
      CommandExecuted event = observer.getEvent();
      assertNotNull(event);
      assertEquals(CommandExecuted.Status.SUCCESS, event.getStatus());
      CommandMetadata command = event.getCommand();
      assertNotNull(command);
      assertEquals("motp", command.getName());
      Object[] parameters = event.getParameters();
      assertNotNull(parameters);
      assertEquals(1, parameters.length);
      assertEquals("motp", parameters[0]);
   }

}
