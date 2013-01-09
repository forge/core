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
import org.jboss.aesh.cl.ParserGenerator;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIInput;

import java.util.List;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ShellCommand implements Completion {

    private Console console;
    private CommandLineParser parser;
    private UICommand command;
    private ShellContext context;

    public ShellCommand(UICommand command) throws Exception {
        this.command = command;
        this.context = new ShellContext();
        command.initializeUI(context);
        generateParser(command);
    }

    public void setConsole(Console console) {
        this.console = console;
    }

    public Console getConsole() {
        return console;
    }

    public ShellContext getContext() {
        return context;
    }

    public void generateParser(UICommand command) {
        ParserBuilder builder = new ParserBuilder();
        for(UIInput<?> input : context.getInputs()) {
            builder.addParameter(new ParameterInt(input.getName(), null));

        }
        parser = builder.generateParser();
        //here we generate the CommandLineParser based on command
        //parser = ParserGenerator.generateParser(command.getClass());
    }

    public CommandLine parse(String line) throws IllegalArgumentException {
        return parser.parse(line);
    }

    public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws Exception {
        context.setCommandLine(commandLine);
        Result result = command.execute(context);
        if(result != null &&
                result.getMessage() != null && result.getMessage().length() > 0)
        getConsole().pushToStdOut(result.getMessage());
    }

    public boolean isStandalone() {
        return context.isStandalone();
    }

    @Override
    public void complete(CompleteOperation completeOperation) {
        List<ParameterInt> parameters = parser.getParameters();
        for(ParameterInt param : parameters) {
            if(param.getName().startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate(param.getName());
        }
    }
}
