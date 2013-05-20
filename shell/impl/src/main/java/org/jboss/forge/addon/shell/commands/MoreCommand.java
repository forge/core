/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import java.io.File;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.extensions.more.More;
import org.jboss.forge.addon.shell.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class MoreCommand implements UICommand {

   @Inject
   private UIInputMany<File> arguments;

    @Override
    public UICommandMetadata getMetadata() {
        return Metadata.forCommand(getClass())
                .name("more")
                .description("more — file perusal filter for crt viewing");
    }

    @Override
    public boolean isEnabled(UIContext context) {
        return (context instanceof ShellContext);
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        arguments.setLabel("");
        arguments.setRequired(false);
        builder.add(arguments);
    }

    @Override
    public void validate(UIValidationContext validator) {
    }

    @Override
    public Result execute(UIContext context) throws Exception {
        if(arguments.getValue() != null &&
                context instanceof ShellContext) {
                    Iterator<File> iter = arguments.getValue().iterator();

            Console console = ((ShellContext) context).getShell().getConsole();
            File file = arguments.getValue().iterator().next();
            //a simple hack that we should try to avoid
            //probably the converter that add the cwd dir if the
            //user input start with ~/
            if(file.getAbsolutePath().contains("~/")) {
                file = new File(Config.getHomeDir()+file.getAbsolutePath().substring(
                        file.getAbsolutePath().indexOf("~/")+1));
            }
            if(file.isFile()) {
                More more = new More(console);
                more.setFile(file);
                more.attach(((ShellContext) context).getConsoleOutput());
                return Results.success();
            }
            else if(file.isDirectory()) {
                return Results.fail(
                        Config.getLineSeparator()+
                                "*** "+ file.getAbsolutePath() + " directory ***"+
                Config.getLineSeparator()+Config.getLineSeparator());
            }
            else {
                return Results.fail(file.getAbsolutePath() + " No such file or directory");
            }
        }
        else {
            return Results.fail("Missing filename (\"more --help\" for help)");
        }
    }

}
