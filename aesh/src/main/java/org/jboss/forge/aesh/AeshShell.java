/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.aesh.commands.ClearCommand;
import org.jboss.forge.aesh.commands.ListServicesCommand;
import org.jboss.forge.aesh.commands.StopCommand;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.event.Perform;
import org.jboss.forge.container.services.Exported;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Singleton
@Exported
public class AeshShell
{
    private Console console;
    private ConsoleOutput output;
    private String prompt = "[forge-2.0]$ ";


    private List<ShellCommand> commands;

    @Inject
    private ContainerControl containerControl;

    @Inject
    private AddonRegistry registry;

    public void observe(@Observes Perform startup) throws IOException
    {
    }

    public void addCommand(ShellCommand command) {
        command.setConsole(console);
        commands.add(command);
    }

    public void initShell() throws Exception {
        Settings.getInstance().setReadInputrc(false);
        Settings.getInstance().setLogging(true);

        commands = new ArrayList<ShellCommand>();
        console = new Console();

        //internal commands
        addCommand(new ShellCommand(new ListServicesCommand(registry)));
        addCommand(new ShellCommand(new StopCommand(console)));
        addCommand(new ShellCommand(new ClearCommand(console)));
    }

    public void startShell() throws Exception {
        prompt = "[forge-2.0]$ ";

        output = null;
        while ((output = console.read(prompt)) != null)
        {
            CommandLine cl = null;
            for(ShellCommand command : commands) {
                try {
                    cl = command.parse(output.getBuffer());
                    if(cl != null) {
                        //need some way of deciding if the command is standalone
                        if(command.getContext().isStandalone()) {
                            //console.

                        }
                        else {
                            command.run(output, cl);
                            break;
                        }
                    }
                }
                catch (IllegalArgumentException iae) {
                    System.out.println("Command: "+command+", did not match: "+output.getBuffer());
                    //ignored for now
                }
            }
            //if we didnt find any commands matching
            if(cl == null) {
               console.pushToStdOut(output.getBuffer()+": command not found.");
            }
            //hack to just read one and one line when we're testing
            if(Settings.getInstance().getName().equals("test"))
                break;

            if(!console.isRunning()) {
                break;
            }
        }
    }

    public String getPrompt() {
        return prompt;
    }

    public Console getConsole() {
        return console;
    }

    public void stopShell() throws IOException {
        if(console != null)
            console.stop();
        containerControl.stop();
    }

}
