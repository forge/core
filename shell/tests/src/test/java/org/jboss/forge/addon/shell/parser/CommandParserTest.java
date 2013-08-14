/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.exception.RequiredOptionException;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.forge.addon.shell.FooCommand;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ForgeShellImpl;
import org.jboss.forge.addon.shell.aesh.ShellCommand;
import org.junit.Test;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class CommandParserTest
{

   @Test
   public void testShellCommandCompletion() throws Exception
   {
      Shell forgeShell = new ForgeShellImpl();
      ShellCommand command = new ShellCommand(null, forgeShell, new FooCommand());

      CompleteOperation completeOperation = new CompleteOperation("foo-bar -", 8);

      command.complete(completeOperation);
      assertEquals("--name", completeOperation.getCompletionCandidates().get(0));
      assertEquals("--help", completeOperation.getCompletionCandidates().get(1));

      completeOperation = new CompleteOperation("foo-bar ", 7);

      command.complete(completeOperation);
      assertEquals("--name", completeOperation.getCompletionCandidates().get(0));
      assertEquals("--help", completeOperation.getCompletionCandidates().get(1));

      completeOperation = new CompleteOperation("foo-bar --na", 12);
      command.complete(completeOperation);
      assertEquals("--name", completeOperation.getCompletionCandidates().get(0));

      completeOperation = new CompleteOperation("foo-bar --name", 14);
      command.complete(completeOperation);
      assertEquals("--name", completeOperation.getCompletionCandidates().get(0));

      completeOperation = new CompleteOperation("foo-bar --h", 14);
      command.complete(completeOperation);
      assertEquals("--help", completeOperation.getCompletionCandidates().get(0));

      completeOperation = new CompleteOperation("foo-bar --b", 14);
      command.complete(completeOperation);
      assertEquals("--bool", completeOperation.getCompletionCandidates().get(0));
      assertEquals("--bar", completeOperation.getCompletionCandidates().get(1));
      assertEquals("--bar2", completeOperation.getCompletionCandidates().get(2));

       completeOperation = new CompleteOperation("foo-bar --name ", 15);
       command.complete(completeOperation);
       assertEquals("BAR", completeOperation.getCompletionCandidates().get(0));

       completeOperation = new CompleteOperation("foo-bar --name B",16);
       command.complete(completeOperation);
       assertEquals("AR", completeOperation.getCompletionCandidates().get(0));

       completeOperation = new CompleteOperation("foo-bar --help ",16);
       command.complete(completeOperation);
       assertEquals("HELP", completeOperation.getCompletionCandidates().get(0));
       assertEquals("HALP", completeOperation.getCompletionCandidates().get(1));
   }

   @Test
   public void testShellCommandParse() throws Exception
   {
      Shell forgeShell = new ForgeShellImpl();
      ShellCommand command = new ShellCommand(null, forgeShell, new FooCommand());

      CommandLine cl = command.parse("foo-bar --name FOO --help halp --bar BAR");

      assertEquals("FOO", cl.getOptionValue("name"));
      assertEquals("halp", cl.getOptionValue("help"));

      cl = command.parse("foo-bar --name FOO --help halp --targetLocation /tmp --bar BAR");

      assertEquals("FOO", cl.getOptionValue("name"));
      assertEquals("halp", cl.getOptionValue("help"));
      assertEquals("/tmp", cl.getOptionValue("targetLocation"));

      try
      {
         cl = command.parse("foo-bar --name FOO --help halp --targetLocation /tmp");
         fail();
      }
      catch (RequiredOptionException iae)
      {
      }
   }

}
