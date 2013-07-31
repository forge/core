/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.inject.Singleton;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.aesh.terminal.TestTerminal;
import org.jboss.forge.addon.shell.spi.AeshSettingsProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@Singleton
public class TestAeshSettingsProvider implements AeshSettingsProvider
{
   
   private PipedOutputStream stdin = new PipedOutputStream();
   private ByteArrayOutputStream stdout = new ByteArrayOutputStream();
   private ByteArrayOutputStream stderr = new ByteArrayOutputStream();
   private PipedInputStream inputStream;
   
   private Settings settings;

   @Override
   public Settings buildAeshSettings()
   {
      try {
         inputStream = new PipedInputStream(stdin);
         settings = new SettingsBuilder()
            .inputStream(inputStream)
            .outputStream(stdout)
            .outputStreamError(stderr)
            .name("test")
            .logging(true)
            .terminal(new TestTerminal())
            .create();
         settings.getOperationManager().addOperation(new KeyOperation(Key.ENTER, Operation.NEW_LINE));
      } catch (IOException e) {
         throw new RuntimeException("Could not configure Shell.", e);
      }
      return settings;
   }
   
   public synchronized OutputStream getStdIn()
   {
      return stdin;
   }

   public synchronized ByteArrayOutputStream getStdOut()
   {
      return stdout;
   }

   public synchronized ByteArrayOutputStream getStdErr()
   {
      return stderr;
   }

}
