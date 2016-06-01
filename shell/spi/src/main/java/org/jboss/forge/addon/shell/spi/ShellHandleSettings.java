/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.spi;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import org.jboss.forge.addon.ui.UIDesktop;

/**
 * A parameter object to initialize the shell
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellHandleSettings
{
   private File currentResource;
   private InputStream stdIn;
   private PrintStream stdOut;
   private PrintStream stdErr;
   private Terminal terminal;
   private String name;
   private UIDesktop desktop;

   public ShellHandleSettings()
   {
   }

   public String name()
   {
      return name;
   }

   public ShellHandleSettings name(String name)
   {
      this.name = name;
      return this;
   }

   public File currentResource()
   {
      return currentResource;
   }

   public ShellHandleSettings currentResource(File currentResource)
   {
      this.currentResource = currentResource;
      return this;
   }

   public InputStream stdIn()
   {
      return stdIn;
   }

   public ShellHandleSettings stdIn(InputStream stdIn)
   {
      this.stdIn = stdIn;
      return this;
   }

   public PrintStream stdOut()
   {
      return stdOut;
   }

   public ShellHandleSettings stdOut(PrintStream stdOut)
   {
      this.stdOut = stdOut;
      return this;
   }

   public PrintStream stdErr()
   {
      return stdErr;
   }

   public ShellHandleSettings stdErr(PrintStream stdErr)
   {
      this.stdErr = stdErr;
      return this;
   }

   public Terminal terminal()
   {
      return terminal;
   }

   public ShellHandleSettings terminal(Terminal terminal)
   {
      this.terminal = terminal;
      return this;
   }

   public UIDesktop desktop()
   {
      return desktop;
   }

   public ShellHandleSettings desktop(UIDesktop desktop)
   {
      this.desktop = desktop;
      return this;
   }

}