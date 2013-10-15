/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIOutputImpl implements UIOutput
{
   private final AeshConsole console;

   public ShellUIOutputImpl(AeshConsole console)
   {
      this.console = console;
   }

   @Override
   public PrintStream out()
   {
      return console.out();
   }

   @Override
   public PrintStream err()
   {
      return console.err();
   }
}
