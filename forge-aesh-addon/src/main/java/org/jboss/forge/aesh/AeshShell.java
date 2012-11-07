/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.event.Startup;
import org.jboss.forge.container.services.Remote;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Singleton
@Remote
public class AeshShell {


    @Inject
    private ContainerControl containerControl;

    public void observe(@Observes Startup startup) throws IOException {

        setup();

        Console console = new Console();
        String prompt = "[forge-2.0]$ ";

        ConsoleOutput line;
        while ((line = console.read(prompt)) != null) {
            if (line.getBuffer().equalsIgnoreCase("quit") ||
                    line.getBuffer().equalsIgnoreCase("exit") ||
                    line.getBuffer().equalsIgnoreCase("reset")) {
                break;
            }
            if(line.getBuffer().equals("clear"))
                console.clear();
        }
        try {
            console.stop();
            containerControl.stop();
        }
        catch (Exception e) {
        }
    }

    //this need to be read from somewhere else, but for now we
    //set the values here
    private void setup() {
        Settings.getInstance().setReadInputrc(false);
        Settings.getInstance().setLogging(true);
    }

}
