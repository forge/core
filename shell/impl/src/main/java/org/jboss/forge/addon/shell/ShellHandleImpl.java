/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.addon.shell.aesh.ForgeTerminal;
import org.jboss.forge.addon.shell.spi.ShellHandle;
import org.jboss.forge.addon.shell.spi.ShellHandleSettings;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;

/**
 * Holds a shell instance (no need for proxies)
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellHandleImpl implements ShellHandle
{
   @Inject
   private ShellFactory shellFactory;

   private Shell shell;

   @Override
   public void initialize(ShellHandleSettings settings)
   {
      SettingsBuilder settingsBuilder = new SettingsBuilder()
               .inputStream(settings.stdIn())
               .outputStream(settings.stdOut())
               .outputStreamError(settings.stdErr())
               .enableMan(true);
      if (settings.terminal() != null)
      {
         settingsBuilder.terminal(new ForgeTerminal(settings.terminal()));
      }
      this.shell = shellFactory.createShell(settings.currentResource(), settingsBuilder.create());
      ShellImpl shellImpl = (ShellImpl) shell;
      if (settings.name() != null)
      {
         shellImpl.setName(settings.name());
      }
      if (settings.desktop() != null)
      {
         shellImpl.setDesktop(settings.desktop());
      }
      // this will always be embedded
      shellImpl.setEmbedded(true);
   }

   @Override
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

   @Override
   public void addCommandExecutionListener(CommandExecutionListener listener)
   {
      if (shell != null)
      {
         shell.addCommandExecutionListener(listener);
      }
   }

}
