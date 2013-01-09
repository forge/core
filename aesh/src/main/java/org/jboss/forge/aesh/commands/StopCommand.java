/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.console.Console;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.*;
import org.jboss.forge.ui.impl.UIInputImpl;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class StopCommand implements UICommand {

    private Console console;

    private UIInput<String> exit;
    private UIInput<String> quit;

    public StopCommand(Console console) {
        setConsole(console);
    }

    private void setConsole(Console console) {
        this.console = console;
    }

    @Override
    public void initializeUI(UIContext context) throws Exception {
        exit = new UIInputImpl<String>("exit", String.class);
        exit.setLabel("exit");
        exit.setRequired(true);
        context.getUIBuilder().add(exit);

        quit = new UIInputImpl<String>("quit", String.class);
        quit.setLabel("quit");
        quit.setRequired(true);
        context.getUIBuilder().add(quit);

        if(context instanceof ShellContext) {
            ((ShellContext) context).setStandalone(false);
        }
    }

    @Override
    public void validate(UIValidationContext context) {
    }

    @Override
    public Result execute(UIContext context) throws Exception {
        console.stop();
        return Result.success("");
    }

}
