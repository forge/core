/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.util;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.OptionBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIInput;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class CommandLineUtil {


    public static CommandLineParser generateParser(UICommand command, ShellContext context) {
        ParserBuilder builder = new ParserBuilder();

               ParameterInt parameter =
                new ParameterInt(command.getId().getName(), command.getId().getDescription());
        for(UIInput<?> input : context.getInputs()) {
            if(input.getValueType() == Boolean.class) {
                parameter.addOption(
                        new OptionBuilder().name(input.getName().charAt(0)).
                                longName(input.getName()).hasValue(false).description(input.getLabel()).create());
            }
            else {
                parameter.addOption(
                        new OptionBuilder().name(input.getName().charAt(0)).
                                longName(input.getName()).description(input.getLabel()).create());
            }
        }
        builder.addParameter(parameter);
        return builder.generateParser();
    }

    public static void populateUIInputs(CommandLine commandLine, ShellContext context) {
        for(UIInput<?> input : context.getInputs()) {
            //String value;
            //if((value = commandLine.getOptionValue(input.getName())) != null)
            if(commandLine.hasOption(input.getName())) {
                String value = commandLine.getOptionValue(input.getName());
                if(value == null) {
                    //if its a boolean we have the option and set it to true
                    if(input.getValueType() == Boolean.class)
                        value = "true";
                    else
                        value = "";
                }
                setInputValue(input, value);
            }
            else
                input.setValue(null);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void setInputValue(UIInput<?> input, String value) {

        if(input.getValueType() == String.class) {
            ((UIInput<String>) input).setValue(value);

        }
        else if(input.getValueType() == Boolean.class) {
            ((UIInput<Boolean>) input).setValue(Boolean.parseBoolean(value));
        }
        else if(input.getValueType() == Integer.class) {
            ((UIInput<Integer>) input).setValue(Integer.parseInt(value));
        }
        else
            input.setValue(null);
    }
}
