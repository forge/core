/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.OptionBuilder;
import org.jboss.aesh.cl.ParameterBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class StopCommand extends ForgeCommand {

    private CommandLineParser parser;

    private List<String> names = new ArrayList<String>();

    public StopCommand(Console console) {
        setConsole(console);
        names.add("exit");
        names.add("quit");
        createParsers();
    }

    @Override
    public CommandLine parse(String line) throws IllegalArgumentException {
        return parser.parse(line);
    }

    @Override
    public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws IOException {
        //not doing much other than stopping the console
        getConsole().stop();
    }

    public void complete(CompleteOperation completeOperation) {
        for(String name : names) {
            if(name.startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate(name);
        }
    }

    private void createParsers() {
        ParserBuilder builder = new ParserBuilder();
        for(String name : names)
            builder.addParameter(new ParameterBuilder().name(name).generateParameter());

        parser = builder.generateParser();
    }
}
