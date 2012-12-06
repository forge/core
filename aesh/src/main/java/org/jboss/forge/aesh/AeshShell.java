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
import org.jboss.forge.aesh.commands.ForgeCommand;
import org.jboss.forge.aesh.commands.ListServicesCommand;
import org.jboss.forge.aesh.commands.StopCommand;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.event.Perform;
import org.jboss.forge.container.services.Remote;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Singleton
@Remote
public class AeshShell
{
   private Console console;
   private ConsoleOutput output;

   private List<ForgeCommand> commands;

   @Inject
   private ContainerControl containerControl;

   @Inject
   private AddonRegistry registry;

   public void observe(@Observes Perform startup) throws IOException
   {
   }

    public void addCommand(ForgeCommand command) {
        command.setConsole(console);
        commands.add(command);
    }

    public void initShell() throws IOException {
        Settings.getInstance().setReadInputrc(false);
        Settings.getInstance().setLogging(true);

        commands = new ArrayList<ForgeCommand>();
        console = new Console();

        //internal commands
        commands.add(new StopCommand(console));
        commands.add(new ClearCommand(console));
        commands.add(new ListServicesCommand(console, registry));
    }

   public void startShell() throws IOException {
      String prompt = "[forge-2.0]$ ";

       output = null;
       while ((output = console.read(prompt)) != null)
       {
           CommandLine cl = null;
           for(ForgeCommand command : commands) {
               try {
                   cl = command.parse(output.getBuffer());
                   if(cl != null) {
                       command.run(output, cl);
                       break;
                   }
               }
               catch (IllegalArgumentException iae) {
                   System.out.println("Command: "+command+", did not match: "+output.getBuffer());
                   //ignored for now
               }
           }
           //hack to just read one and one line when we're testing
           if(Settings.getInstance().getName().equals("test"))
               break;

           if(!console.isRunning()) {
               break;
           }
       }
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
