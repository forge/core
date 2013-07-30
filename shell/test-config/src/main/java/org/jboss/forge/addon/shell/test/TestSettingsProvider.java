package org.jboss.forge.addon.shell.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.aesh.terminal.TestTerminal;
import org.jboss.forge.addon.shell.spi.AeshSettingsProvider;

public class TestSettingsProvider implements AeshSettingsProvider
{
   
   private PipedOutputStream stdin = new PipedOutputStream();
   private ByteArrayOutputStream stdout = new ByteArrayOutputStream();
   private ByteArrayOutputStream stderr = new ByteArrayOutputStream();
   private PipedInputStream inputStream;
   
   private Settings settings;

   @Override
   public Settings buildSettings()
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
