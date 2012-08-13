/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.fsh;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.fshparser.FSHParser;
import org.jboss.forge.shell.command.fshparser.FSHRuntime;
import org.jboss.forge.shell.command.fshparser.Parse;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mike Brock .
 */
@RunWith(Arquillian.class)
public class FSHBasicTests extends AbstractShellTest
{
   @Inject
   public FSHRuntime runtime;

   @Test
   @Ignore
   public void testEqualsInString() throws Exception
   {
      getShell().execute("echo hello=world");
   }

   @Test
   @Ignore
   public void testAtInString() throws Exception
   {
      getShell().execute("echo git@github.com");
   }

   @Test
   public void testSimple()
   {
      runtime.run("@dir = '/'; for (i=0;i<4;i++) { ls -l $dir }");
   }

   @Test
   public void testSimple2()
   {
      runtime.run("@foo = 0; while (foo < 2) { if (foo == 1) { ls -l /; @foo++; } else { ls /; @foo++; };  }");
   }

   @Test
   public void testSimple3()
   {
      runtime.run("if (true) { ls -l (2 * 2) } else { ls / | cat }");
   }

   @Test
   public void testSimple4()
   {
      runtime.run("if (false) { ls -l (2 * 2) } else if (false) { ls / } else if (true) { @System.out.println('Hello') }");
   }

   @Test
   public void testSimple5()
   {
      runtime.run("@MySetting=true; if (MySetting) { @System.out.println('Yep (' + MySetting + ')'); }");
   }

   @Test
   public void testSimple6()
   {
      runtime.run("for (dir : ['/', '~', '..']) { ls -l $dir | wc -l }");
   }

   @Test
   public void testSimple7()
   {
      runtime.run("ls -l /; ls");
   }

   @Test
   public void testSimple8()
   {
      runtime.run("ls -l *.txt");
   }

   @Test
   public void testSimple10()
   {
      runtime.run("@myVar='ls'; echo $myVar.toUpperCase()");
   }

   // @Test
   // public void testSimple11()
   // {
   // runtime.run("for (i=0;i<1000;i++) { ls }");
   // }

   @Test
   public void testSimple12()
   {
      runtime.run("for (i = 0; i < 2; i++) { @System.out.println(\"foo\"); }");
   }

   @Test
   public void testSimple13()
   {
      runtime.run("if (isdef $FOO) { }");
   }

   @Test
   public void testSimple14()
   {
      runtime.run("echo \"$PROMPT\"");
   }

   @Test
   public void testExpressionLoop()
   {
      runtime.run("for (file : new java.io.File(\".\").listFiles()) { echo $file.getName().toUpperCase(); }");
   }

   @Test
   public void testStatement()
   {
      runtime.run("@System.out.println('hello')");
   }

   @Test
   public void testParse()
   {
      String s = Parse.disassemble(new FSHParser("for (i=0;i<4;i++) { ls -l $dir }").parse());

      System.out.println(s);
   }

   @Test
   public void testLargeNest()
   {
      runtime.run("@NO_MOTD = false;\n\n"
               +
               "if ($NO_MOTD) {    \n"
               +
               "   echo \"   ____                          _____                    \";\n"
               +
               "   echo \"  / ___|  ___  __ _ _ __ ___    |  ___|__  _ __ __ _  ___ \";\n"
               +
               "   echo \"  \\\\___ \\\\ / _ \\\\/ _` | '_ ` _ \\\\   | |_ / _ \\\\| '__/ _` |/ _ \\\\  \\c{yellow}\\\\\\\\\\c\";\n"
               +
               "   echo \"   ___) |  __/ (_| | | | | | |  |  _| (_) | | | (_| |  __/  \\c{yellow}//\\c\";\n" +
               "   echo \"  |____/ \\\\___|\\\\__,_|_| |_| |_|  |_|  \\\\___/|_|  \\\\__, |\\\\___| \";\n" +
               "   echo \"                                                |___/      \";\n" +
               "}");

   }

}
