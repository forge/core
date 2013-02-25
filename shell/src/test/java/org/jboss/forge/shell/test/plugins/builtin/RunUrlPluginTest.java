/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RunUrlPluginTest extends AbstractShellTest
{
   private static Server server;

   @BeforeClass
   public static void setupHttpServer() throws Exception
   {
      server = new Server(18080);
      server.setHandler(new AbstractHandler()
      {

         @Override
         public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                  throws IOException, ServletException
         {
            if (target.equals("/RunUrlPluginTest.fsh"))
            {
               response.getOutputStream().write("echo 'run-url plugin test';".getBytes());
            }
            else
            {
               response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
         }
      });
      server.start();
   }

   @Test
   public void testRunScriptFromHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url http://localhost:18080/RunUrlPluginTest.fsh");
   }

   @Test(expected = RuntimeException.class)
   public void testRunScriptNonHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url new_file_invented.script");
   }

   @Test
   public void testRunScriptNotFoundHttpUrl() throws Exception
   {
      Shell shell = getShell();

      shell.execute("run-url http://localhost:18080/not-found-file");
   }

   @AfterClass
   public static void tearDownHttpServer() throws Exception
   {
      server.stop();
   }

}
