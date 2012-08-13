/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.fsh;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.fshparser.FSHRuntime;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mike Brock
 */
@Ignore
@RunWith(Arquillian.class)
public class FSHErrorsTests extends AbstractShellTest
{
   @Inject
   public FSHRuntime runtime;

   @Test
   public void testSimple()
   {
      runtime.run("if ($foo) {");
   }

}
