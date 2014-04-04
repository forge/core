/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import org.jboss.forge.addon.ui.command.CommandExecutionListener;

/**
 * Holds a shell instance (no need for proxies)
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ShellHandle
{
   public void initialize(File currentResource, InputStream stdIn, PrintStream stdOut, PrintStream stdErr);

   public void destroy();

   public void addCommandExecutionListener(CommandExecutionListener listener);

}
