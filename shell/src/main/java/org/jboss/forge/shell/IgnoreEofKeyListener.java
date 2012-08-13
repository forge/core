/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.shell.events.Shutdown;
import org.jboss.forge.shell.events.Shutdown.Status;
import org.jboss.forge.shell.integration.KeyListener;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class IgnoreEofKeyListener implements KeyListener
{
   int ignoreEOFs = 0;
   int numEOFs = 0;
   private final Shell shell;
   private final Event<Shutdown> shutdown;

   @Inject
   public IgnoreEofKeyListener(final Shell shell, final Event<Shutdown> shutdown)
   {
      ForgeEnvironment environment = shell.getEnvironment();
      String eofs = (String) environment.getProperty(ShellImpl.PROP_IGNORE_EOF);

      int propEOFs;
      try
      {
         propEOFs = Integer.parseInt(eofs);
      }
      catch (NumberFormatException e)
      {
         if (shell.isVerbose())
            ShellMessages.info(shell, "Unable to parse Shell property [" + ShellImpl.PROP_IGNORE_EOF + "]");

         propEOFs = ShellImpl.DEFAULT_IGNORE_EOF;
      }

      this.ignoreEOFs = propEOFs;
      this.shell = shell;
      this.shutdown = shutdown;
   }

   @Override
   public boolean keyPress(final int key)
   {
      if (!shell.isExecuting() && (key == 4))
      {
         if (this.numEOFs < ignoreEOFs)
         {
            shell.println();
            shell.println("(Press CTRL-D again or type 'exit' to quit.)");
            this.numEOFs++;
         }
         else
         {
            shell.print("exit");
            shutdown.fire(new Shutdown(Status.NORMAL));
         }
         return true;
      }
      return false;
   }

   public void reset()
   {
      this.numEOFs = 0;
   }

}
