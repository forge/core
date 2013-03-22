/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.OptionBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UIInputMany;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class CommandLineUtil {

    private static ConverterFactory converterFactory = null;

    public static CommandLineParser generateParser(UICommand command, ShellContext context) {
        ParserBuilder builder = new ParserBuilder();

               ParameterInt parameter =
                new ParameterInt(command.getMetadata().getName(), command.getMetadata().getDescription());
        for(InputComponent<?, ?> input : context.getInputs()) {
            if(!input.getName().equals("arguments")) {
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
        }
        builder.addParameter(parameter);
        return builder.generateParser();
    }

    public static void populateUIInputs(CommandLine commandLine,
                                        ShellContext context, AddonRegistry registry) {
        for(InputComponent<?, ?> input : context.getInputs()) {
            if(commandLine.hasOption(input.getName()) &&
                    input instanceof UIInput) {
                String value = commandLine.getOptionValue(input.getName());
                setInputValue((UIInput<Object>) input, value, registry);
            }
            else if(input.getName().equals("arguments") &&
                    input instanceof UIInputMany) {
                setInputManyValue((UIInputMany) input, commandLine.getArguments(), registry);
            }
            else if(commandLine.hasOption(input.getName()) &&
                    input instanceof UIInputMany) {
                //TODO
                //String value = commandLine.getOptionValue(input.getName());
                //setInputManyValue((UIInputMany) input, commandLine.getArguments(), registry);
            }
            else
                ((UIInput) input).setValue(null);

        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void setInputManyValue(final UIInputMany<?> input, List<String> values,
                                          AddonRegistry registry) {

        if(values != null && !values.isEmpty()) {
            List list = new ArrayList(values.size());
            if(converterFactory == null)
                converterFactory =  registry.getExportedInstance(ConverterFactory.class).get();
            for(String s : values) {
                Object convertedType = s;
                Class<? extends Object> source = s.getClass();
                Class<Object> target = (Class<Object>) input.getValueType();
                Converter converter = converterFactory.getConverter(source, target);
                convertedType = converter.convert(s);

                list.add(convertedType);
            }

            input.setValue(list);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void setInputValue(final UIInput<Object> input, Object value,
                                      AddonRegistry registry) {
        Object convertedType = value;
        if (value != null) {
            if(converterFactory == null)
                converterFactory = registry.getExportedInstance(ConverterFactory.class).get();
            Class<? extends Object> source = value.getClass();
            Class<Object> target = input.getValueType();
            if (converterFactory != null) {
                Converter converter = converterFactory.getConverter(source, target);
                convertedType = converter.convert(value);
            }
            else {
                System.err.println("Converter Factory was not deployed !! Cannot convert from " + source + " to " + target);
            }
        }
        input.setValue(convertedType);
    }

}
