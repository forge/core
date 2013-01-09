/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParameterBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;

import java.io.IOException;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ClearCommand extends ForgeCommand {

    private CommandLineParser parser;

    private String name = "clear";

    public ClearCommand(Console console) {
        setConsole(console);
        createParsers();
    }

    @Override
    public CommandLine parse(String line) throws IllegalArgumentException {
        return parser.parse(line);
    }

    @Override
    public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws IOException {
        getConsole().clear();
    }

    public void complete(CompleteOperation completeOperation) {
        if(name.startsWith(completeOperation.getBuffer()))
            completeOperation.addCompletionCandidate(name);
    }

    private void createParsers() {
        parser = new ParserBuilder(
                new ParameterBuilder().name(name).generateParameter()).generateParser();
    }
}
