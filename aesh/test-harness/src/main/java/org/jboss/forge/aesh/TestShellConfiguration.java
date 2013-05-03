/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.inject.Singleton;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.aesh.terminal.TestTerminal;
import org.jboss.forge.aesh.spi.ShellConfiguration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class TestShellConfiguration implements ShellConfiguration
{
   private PipedOutputStream stdin = new PipedOutputStream();
   private ByteArrayOutputStream stdout = new ByteArrayOutputStream();
   private ByteArrayOutputStream stderr = new ByteArrayOutputStream();

   public TestShellConfiguration()
   {
      System.out.println("Created new: " + this);
   }

   @Override
   public synchronized void configure()
   {
      try
      {
         reset();
         Settings.getInstance().setInputStream(new PipedInputStream(stdin));
         Settings.getInstance().setStdOut(stdout);
         Settings.getInstance().setStdErr(stderr);
         Settings.getInstance().setName("test");
         Settings.getInstance().setTerminal(new TestTerminal());
         Settings.getInstance().getOperationManager().addOperation(new KeyOperation(Key.ENTER, Operation.NEW_LINE));
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not configure Shell.", e);
      }
   }

   private void reset()
   {
      stdin = new PipedOutputStream();
      stdout = new ByteArrayOutputStream();
      stderr = new ByteArrayOutputStream();
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
