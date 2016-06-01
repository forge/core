/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.spi;

import org.jboss.forge.addon.ui.command.CommandExecutionListener;

/**
 * Holds a shell instance (no need for proxies)
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ShellHandle
{
   void initialize(ShellHandleSettings settings);

   void destroy();

   void addCommandExecutionListener(CommandExecutionListener listener);
}
