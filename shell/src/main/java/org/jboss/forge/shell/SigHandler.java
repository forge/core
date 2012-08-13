/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings("restriction")
public class SigHandler
{
   public static void init(final ShellImpl shell)
   {
      SignalHandler interruptHandler = new SignalHandler()
      {
         @Override
         public void handle(final Signal signal)
         {
            shell.interrupt();
         }
      };

      Signal.handle(new Signal("INT"), interruptHandler);

   }

}
