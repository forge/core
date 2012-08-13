/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.completer;

import javax.inject.Singleton;

import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.test.plugins.builtin.MockFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
@RequiresFacet(MockFacet.class)
public class MockCompleterPlugin4 implements Plugin
{
   @SetupCommand
   public void setup()
   {

   }

   @Command
   public void other()
   {

   }
}
