/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
