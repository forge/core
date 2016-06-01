/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.forge.addon.shell.ShellMessages;
import org.jboss.forge.addon.ui.progress.DefaultUIProgressMonitor;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * A {@link UIProgressMonitor} implementation for the shell
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIProgressMonitor extends DefaultUIProgressMonitor
{
   private final PrintStream out;

   public ShellUIProgressMonitor(PrintStream out)
   {
      this.out = out;
   }

   @Override
   public void beginTask(String name, int totalWork)
   {
      super.beginTask(name, totalWork);
      String message = String.format("%s \t[%d/%d] ...", name, getCurrentWork() + 1, getTotalWork());
      ShellMessages.info(out, message);
   }

   @Override
   public void subTask(String name)
   {
      super.subTask(name);
      String message = String.format("%s:%s \t[%d/%d] ...", getTaskName(), name, getCurrentWork() + 1, getTotalWork());
      ShellMessages.info(out, message);
   }

   @Override
   public void setTaskName(String name)
   {
      super.setTaskName(name);
      String message = String.format("%s \t[%d/%d] ...", name, getCurrentWork() + 1, getTotalWork());
      ShellMessages.info(out, message);
   }
}
