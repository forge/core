/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.complete.CompleteOperation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ShellCommandTest {


    @Test
    public void testShellCommandCompletion() throws Exception {
        ForgeShell forgeShell = new ForgeShell();
        ShellCommand command = new ShellCommand(new FooCommand(), forgeShell);

        CompleteOperation completeOperation = new CompleteOperation("foo-bar -",8);

        command.complete(completeOperation);
        assertEquals("--name", completeOperation.getCompletionCandidates().get(0));
        assertEquals("--help", completeOperation.getCompletionCandidates().get(1));

        completeOperation = new CompleteOperation("foo-bar ",7);

        command.complete(completeOperation);
        assertEquals("--name", completeOperation.getCompletionCandidates().get(0));
        assertEquals("--help", completeOperation.getCompletionCandidates().get(1));

        completeOperation = new CompleteOperation("foo-bar --na",12);
        command.complete(completeOperation);
        assertEquals("--name", completeOperation.getCompletionCandidates().get(0));

        completeOperation = new CompleteOperation("foo-bar --name",14);
        command.complete(completeOperation);
        assertEquals("--name", completeOperation.getCompletionCandidates().get(0));

        completeOperation = new CompleteOperation("foo-bar --h",14);
        command.complete(completeOperation);
        assertEquals("--help", completeOperation.getCompletionCandidates().get(0));

        completeOperation = new CompleteOperation("foo-bar --b",14);
        command.complete(completeOperation);
        assertEquals("--bool", completeOperation.getCompletionCandidates().get(0));
        assertEquals("--bar", completeOperation.getCompletionCandidates().get(1));
        assertEquals("--bar2", completeOperation.getCompletionCandidates().get(2));

        /*
        completeOperation = new CompleteOperation("foo-bar --bar ",14);
        command.complete(completeOperation);
        System.out.println(completeOperation);
        */
    }

    @Test
    public void testShellCommandParse() throws Exception {
        ForgeShell forgeShell = new ForgeShell();
        ShellCommand command = new ShellCommand(new FooCommand(), forgeShell);

        CommandLine cl = command.parse("foo-bar --name FOO --help halp --bar BAR");

        assertEquals("FOO", cl.getOptionValue("name"));
        assertEquals("halp", cl.getOptionValue("help"));

        cl = command.parse("foo-bar --name FOO --help halp --targetLocation /tmp --bar BAR");

        assertEquals("FOO", cl.getOptionValue("name"));
        assertEquals("halp", cl.getOptionValue("help"));
        assertEquals("/tmp", cl.getOptionValue("targetLocation"));


        try {
            cl = command.parse("foo-bar --name FOO --help halp --targetLocation /tmp");
            fail();
        }
        catch (IllegalArgumentException iae) {
        }

    }

}
