/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import org.jboss.forge.shell.ShellPrintWriter;

/**
 * @author Mike Brock .
 */
public interface PipeOut extends ShellPrintWriter
{
   public boolean isPiped();

   public void setPiped(boolean v);

   public String getBuffer();
}
