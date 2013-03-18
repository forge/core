/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.command;

import javax.enterprise.event.Observes;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.events.PreCommandExecution;

/**
 * An observer that displays a warning when a deprecated command method is about to be executed
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class DeprecatedObserver
{

   public void beforeCommandExecution(@Observes PreCommandExecution cmd, Shell shell)
   {
      CommandMetadata command = cmd.getCommand();
      boolean deprecated = command.getMethod().isAnnotationPresent(Deprecated.class);
      if (deprecated)
      {
         String message = String.format("The command (%s) is deprecated and may be removed in future versions",
                  command.getName());
         ShellMessages.warn(shell, message);
      }
   }

}
