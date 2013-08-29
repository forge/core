/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCommand;
import org.jboss.aesh.extensions.harlem.Harlem;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class HarlemCommand extends AbstractNativeAeshCommand
{
   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata()
               .name("harlem")
               .description("do you want some harlem?");
   }

   @Override
   public ConsoleCommand getConsoleCommand(ShellContext shellContext)
   {
      Console console = shellContext.getProvider().getConsole();
      Harlem harlem = new Harlem(console);
      return harlem;
   }
}
