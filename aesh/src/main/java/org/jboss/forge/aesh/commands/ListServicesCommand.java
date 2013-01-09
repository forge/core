/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParameterBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ListServicesCommand extends ForgeCommand {

    private AddonRegistry registry;

    private CommandLineParser parser;

    private String name = "list-services";

    public ListServicesCommand(Console console, AddonRegistry registry) {
        setConsole(console);
        this.registry = registry;
        createParsers();
    }

    @Override
    public CommandLine parse(String line) throws IllegalArgumentException {
        return parser.parse(line);
    }

    @Override
    public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws IOException {
        listServices();
    }

    @Override
    public void complete(CompleteOperation completeOperation) {
       if(name.startsWith(completeOperation.getBuffer()))
           completeOperation.addCompletionCandidate(name);
    }

    private void createParsers() {
        parser = new ParserBuilder(
                new ParameterBuilder().name(name).generateParameter()).generateParser();
    }

    private void listServices() throws IOException
    {
        Set<Addon> addons = registry.getRegisteredAddons();
        System.out.println("listing addons: "+addons);
        for (Addon addon : addons)
        {
            Set<Class<?>> serviceClasses = addon.getServiceRegistry().getServices();
            for (Class<?> type : serviceClasses)
            {
                System.out.println("NAME: "+type.getName());
                getConsole().pushToStdOut(type.getName());
                for (Method method : type.getMethods())
                {
                    getConsole().pushToStdOut("\n\t - " + getName(method));
                }
                getConsole().pushToStdOut("\n");
            }
        }
    }

   public String getName(Method method)
   {
      String params = "(";
      List<Class<?>> parameters = Arrays.asList(method.getParameterTypes());

      Iterator<Class<?>> iterator = parameters.iterator();
      while (iterator.hasNext())
      {
         Class<?> p = iterator.next();
         params += p.getName();

         if (iterator.hasNext())
         {
            params += ",";
         }
      }

      params += ")";

      String returnType = method.getReturnType().getName() == null ? "void" : method.getReturnType().getName();
      return method.getName() + params + "::" + returnType;
   }

}
