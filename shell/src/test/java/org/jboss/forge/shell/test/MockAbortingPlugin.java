/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.exceptions.AbortedException;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
@Alias("test-map")
public class MockAbortingPlugin implements Plugin
{
   private boolean aborted = false;
   private boolean executed = false;

   @Inject
   ShellPrompt prompt;

   @DefaultCommand
   public void run()
   {
      try
      {
         this.executed = true;
         this.aborted = false;
         String value = prompt.prompt("Waiting for EOF");
         System.out.println(value);
      }
      catch (AbortedException e)
      {
         this.aborted = true;
      }
   }

   public boolean isAborted()
   {
      return aborted;
   }

   public boolean isExecuted()
   {
      return executed;
   }
}
