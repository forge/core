/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.impl;

import java.io.PrintStream;

class UncloseablePrintStream extends PrintStream
{
   public UncloseablePrintStream(PrintStream stream)
   {
      super(stream, true);
   }

   @Override
   public void close()
   {
      // Uncloseable
   }
}