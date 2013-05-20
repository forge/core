/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.IOException;

import org.jboss.aesh.console.Console;
import org.jboss.forge.furnace.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ForgeShell
{
   public void startShell() throws Exception;

   public String getPrompt();

   public Console getConsole();

   public void stopShell() throws IOException;
}
