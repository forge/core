/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.completer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.completer.CompletedCommandHolder;
import org.jboss.forge.shell.completer.OptionAwareCompletionHandler;
import org.jboss.forge.shell.console.jline.Terminal;
import org.jboss.forge.shell.console.jline.UnsupportedTerminal;
import org.jboss.forge.shell.console.jline.console.ConsoleReader;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@RunWith(Arquillian.class)
public class OptionAwareCompletionHandlerTest extends AbstractShellTest
{

   @Inject
   private CompletedCommandHolder commandHolder;

   private ConsoleReader consoleReader;

   @Before
   public void setup() throws Exception
   {
      InputStream in = new ByteArrayInputStream(new byte[256]);
      Terminal terminal = new UnsupportedTerminal();
      consoleReader = new ConsoleReader(in, getShell(), terminal);
   }

   @Test
   public void testComplete() throws Exception
   {

      OptionAwareCompletionHandler completionHandler =
               new OptionAwareCompletionHandler(commandHolder, getShell());
      Assert.assertNotNull(completionHandler);

      List<CharSequence> candidates = new ArrayList<CharSequence>();
      candidates.add("foo");

      consoleReader.getCursorBuffer().write("");
      Assert.assertTrue(completionHandler.complete(consoleReader, candidates, 0));
      Assert.assertEquals("foo", consoleReader.getCursorBuffer().toString());

      consoleReader.getCursorBuffer().clear();
      consoleReader.getCursorBuffer().write("foo");
      Assert.assertFalse(completionHandler.complete(consoleReader, candidates, 0));

      candidates.add("foz");
      consoleReader.getCursorBuffer().clear();
      Assert.assertTrue(completionHandler.complete(consoleReader, candidates, 0));
      Assert.assertEquals("fo", consoleReader.getCursorBuffer().toString());

      candidates.clear();
      candidates.add("f o o ");
      Assert.assertTrue(completionHandler.complete(consoleReader, candidates, 0));
      Assert.assertEquals("f\\ o\\ o ", consoleReader.getCursorBuffer().toString());

      consoleReader.getCursorBuffer().clear();
      consoleReader.getCursorBuffer().write("f\\ o\\ o ");
      Assert.assertFalse(completionHandler.complete(consoleReader, candidates, 0));

      candidates.add("f o z");
      consoleReader.getCursorBuffer().clear();
      Assert.assertTrue(completionHandler.complete(consoleReader, candidates, 0));
      Assert.assertEquals("f\\ o\\ ", consoleReader.getCursorBuffer().toString());

   }

}
