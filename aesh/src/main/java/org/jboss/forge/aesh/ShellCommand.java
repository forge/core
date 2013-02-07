/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.File;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineCompletionParser;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.aesh.util.CommandLineUtil;
import org.jboss.forge.ui.*;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ShellCommand implements Completion {

    private CommandLineParser parser;
    private UICommand command;
    private ShellContext context;

    private ForgeShell aeshell;

    public ShellCommand(UICommand command, ForgeShell aeshell) throws Exception {
        this.command = command;
        this.context = new ShellContext(aeshell);
        this.aeshell = aeshell;
        command.initializeUI(context);
        generateParser(command);
    }

    public Console getConsole() {
        return aeshell.getConsole();
    }

    public ShellContext getContext() {
        return context;
    }

    public ForgeShell getAeshell() {
        return aeshell;
    }

    public UICommand getCommand() {
        return command;
    }

    public void generateParser(UICommand command) {
        parser = CommandLineUtil.generateParser(command, context);
    }

    public CommandLine parse(String line) throws IllegalArgumentException {
        return parser.parse(line);
    }

    public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws Exception {
        CommandLineUtil.populateUIInputs(commandLine, context, getAeshell().getRegistry());
        Result result = command.execute(context);
        if(result != null &&
                result.getMessage() != null && result.getMessage().length() > 0)
        getConsole().pushToStdOut(result.getMessage()+ Config.getLineSeparator());
    }

    public boolean isStandalone() {
        return context.isStandalone();
    }

    @Override
    public void complete(CompleteOperation completeOperation) {
        ParameterInt param = parser.getParameters().get(0);
        //complete command names
        if(param.getName().startsWith(completeOperation.getBuffer()))
            completeOperation.addCompletionCandidate(param.getName());
            //complete options/arguments
        else if(completeOperation.getBuffer().startsWith(param.getName())) {
            ParsedCompleteObject completeObject =
                    new CommandLineCompletionParser(parser).findCompleteObject(completeOperation.getBuffer());
            if(completeObject.doDisplayOptions()) {
                if(param.getOptionLongNamesWithDash().size() > 1) {
                    completeOperation.addCompletionCandidates( param.getOptionLongNamesWithDash());
                }
                else {
                    completeOperation.addCompletionCandidates( param.getOptionLongNamesWithDash());
                    completeOperation.setOffset( completeOperation.getCursor()-
                            completeObject.getOffset());
                }
            }
            //try to complete an options value
            else if(completeObject.isOption()) {
                UIInputComponent inputOption = context.findInput(completeObject.getName());
                //option type == File
                if(inputOption != null && inputOption.getValueType() == File.class) {
                    completeOperation.setOffset(completeOperation.getCursor());
                    if(completeObject.getValue() == null)
                        new FileLister("", new File(System.getProperty("user.dir"))).findMatchingDirectories(completeOperation);
                    else
                        new FileLister(completeObject.getValue(), new File(System.getProperty("user.dir"))).findMatchingDirectories(completeOperation);
                }
                else if(inputOption != null && inputOption.getValueType() == Boolean.class) {
                    //TODO
                }
                //this shouldnt be needed
                if(inputOption != null && inputOption instanceof UIInput) {
                    Iterable<String> iter = ((UIInput) inputOption).getCompleter().getCompletionProposals(inputOption, completeObject.getValue());
                    if(iter != null) {
                        for(String s : iter)
                            completeOperation.addCompletionCandidate(s);
                    }
                    if(completeOperation.getCompletionCandidates().size() == 1) {
                        completeOperation.setOffset( completeOperation.getCursor()-
                                completeObject.getOffset());
                    }
                }
            }
            //try to complete a argument value
            else if(completeObject.isArgument()) {
                UIInputComponent inputOption = context.findInput("arguments"); //default for arguments

                if(inputOption != null && inputOption.getValueType() == File.class) {
                    completeOperation.setOffset(completeOperation.getCursor());
                    if(completeObject.getValue() == null)
                        new FileLister("", new File(System.getProperty("user.dir"))).findMatchingDirectories(completeOperation);
                    else
                        new FileLister(completeObject.getValue(), new File(System.getProperty("user.dir"))).findMatchingDirectories(completeOperation);
                }
                else if(inputOption != null && inputOption.getValueType() == Boolean.class) {
                    //TODO
                }
                else {
                    //this shouldnt be needed
                    if(inputOption != null && inputOption instanceof UIInputMany) {
                        Iterable<String> iter = ((UIInputMany) inputOption).getCompleter().getCompletionProposals(inputOption, completeObject.getValue());
                        if(iter != null) {
                            for(String s : iter) {
                                completeOperation.addCompletionCandidate(s);
                            }
                        }
                        if(completeOperation.getCompletionCandidates().size() == 1) {
                            completeOperation.setOffset( completeOperation.getCursor()-
                                    completeObject.getOffset());
                        }
                    }
                }
            }

        }
    }
}
