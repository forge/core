/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.furnace.services.Imported;

/**
 * Hook for Aesh operations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeConsoleCallback implements ConsoleCallback
{
   @Inject
   private Imported<UICommand> commands;

   /**
    * This method will be called when a user press the "enter/return" key. The return value is to indicate if the
    * outcome was a success or not. Return 0 for success and something else for failure (typical 1 or -1).
    */
   @Override
   public int readConsoleOutput(ConsoleOutput output) throws IOException
   {
      for (UICommand command : commands)
      {
         
      }
      return 0;
   }
}
