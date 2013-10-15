/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import java.io.PrintStream;

import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIOutputImpl implements UIOutput
{
   private final PrintStream out;
   private final PrintStream err;

   public UIOutputImpl(PrintStream out, PrintStream err)
   {
      this.out = out;
      this.err = err;
   }

   @Override
   public PrintStream out()
   {
      return out;
   }

   @Override
   public PrintStream err()
   {
      return err;
   }

}
