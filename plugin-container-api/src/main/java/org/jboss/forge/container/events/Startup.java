/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.events;

import java.io.File;

/**
 * Fired as a signal to the shell to bootstrap and accept user input. Should be fired only once per application runtime
 * unless followed by a subsequent {@link Shutdown} event.
 * <p/>
 * <strong>For example:</strong>
 * <p/>
 * <code>@Inject Event&lt;Startup&gt startup; <br/>...<br/>
 * startup.fire(new Startup());
 * </code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Startup
{
   private File workingDirectory = new File("").getAbsoluteFile();
   private boolean restart;

   public Startup()
   {
   }

   public Startup(final File workingDirectory)
   {
      this.workingDirectory = workingDirectory;
   }

   public Startup(final File workingDirectory, final boolean restart)
   {
      this.workingDirectory = workingDirectory;
      this.restart = restart;
   }

   public File getWorkingDirectory()
   {
      return workingDirectory;
   }

   public boolean isRestart()
   {
      return restart;
   }
}
