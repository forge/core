/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.completer;

import javax.inject.Singleton;

import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class MockCompleterPlugin2 implements Plugin
{
   private boolean command1Invoked = false;

   @DefaultCommand
   public void command1(@Option(description = "Option One", required = true) final int number,
            @Option(required = false) final String optional)
   {
      command1Invoked = true;
   }

   public boolean isCommand1Invoked()
   {
      return command1Invoked;
   }
}
