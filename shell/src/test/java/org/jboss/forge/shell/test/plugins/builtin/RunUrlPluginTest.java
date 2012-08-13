/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import java.net.UnknownHostException;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RunUrlPluginTest extends AbstractShellTest
{

   @Test
   public void testRunScriptFromHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url https://raw.github.com/forge/core/master/shell/src/test/resources/org/jboss/forge/shell/test/plugins/builtin/RunUrlPluginTest.fsh");
   }

   @Test(expected = RuntimeException.class)
   public void testRunScriptNonHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url new_file_invented.script");
   }

   @Ignore
   @Test(expected = UnknownHostException.class)
   public void testRunScriptNotHostHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url http://not-found-host-ffsdf2423.org/not-found-file");
   }

   @Test
   public void testRunScriptNotFoundHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url http://dl.dropbox.com/u/19065548/not-found-file");
   }

}
