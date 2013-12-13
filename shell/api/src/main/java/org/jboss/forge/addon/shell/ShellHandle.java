/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;

/**
 * Holds a shell instance (no need for proxies)
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellHandle
{
   @Inject
   private ShellFactory shellFactory;

   private Shell shell;

   public void initialize(File currentResource, InputStream stdIn, PrintStream stdOut, PrintStream stdErr)
   {
      Settings settings = new SettingsBuilder()
               .inputStream(stdIn)
               .outputStream(stdOut)
               .outputStreamError(stdErr)
               .enableMan(true)
               .create();
      this.shell = shellFactory.createShell(currentResource, settings);
   }

   public void destroy()
   {
      if (this.shell != null)
         try
         {
            this.shell.close();
         }
         catch (Exception e)
         {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Error while closing Shell", e);
         }
   }
}
