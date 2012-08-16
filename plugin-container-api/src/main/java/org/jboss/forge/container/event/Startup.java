/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.event;

import java.io.File;

/**
 * Fired when the container begins its startup process.
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
