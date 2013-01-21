/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.forge.shell.console.jline.TerminalSupport;

public class IdeTerminal extends TerminalSupport
{

   public final int DEFAULT_WIDTH = Integer.MAX_VALUE;

   public IdeTerminal()
   {
      super(true);
      setEchoEnabled(false);
      setAnsiSupported(true);
   }

   public int readCharacter(final InputStream in) throws IOException
   {
      int result = in.read();
      return result == '\r' ? in.read() : result;
   }

   public int getWidth()
   {
      return DEFAULT_WIDTH;
   }

}
