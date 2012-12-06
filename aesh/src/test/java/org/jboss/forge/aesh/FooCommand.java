/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.cl.ParameterBuilder;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.aesh.commands.ForgeCommand;

import java.io.IOException;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class FooCommand extends ForgeCommand {

    private CommandLineParser parser;

    public FooCommand() {
        createParsers();
    }

    @Override
    public CommandLine parse(String line) throws IllegalArgumentException {
        return parser.parse(line);
    }

    @Override
    public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws IOException {
        getConsole().pushToStdOut("boo\n");
    }

    @Override
    public void complete(CompleteOperation completeOperation) {
    }

    private void createParsers() {
        ParameterBuilder foo = new ParameterBuilder().name("foo");
        ParameterBuilder bar = new ParameterBuilder().name("bar");
        ParserBuilder builder = new ParserBuilder(foo.generateParameter());
        builder.addParameter(bar.generateParameter());

        parser = builder.generateParser();
    }

}
