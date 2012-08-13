/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import static java.lang.String.valueOf;
import static org.mvel2.MVEL.eval;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.mvel2.util.StringAppender;

/**
 * @author Mike Brock
 */
@Alias("exec")
@Topic("Shell Environment")
@Help("Executes an expression")
public class ScriptExecPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public ScriptExecPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void execScript(@Option(required = true, description = "expr") final String... expr)
   {
      StringAppender appender = new StringAppender();
      for (String s : expr)
      {
         appender.append(s);
      }

      Object retVal = eval(appender.toString(), new ScriptContext(), shell.getEnvironment().getProperties());

      if (retVal != null)
      {
         shell.println(valueOf(retVal));
      }
   }

   public class ScriptContext
   {
      public void cmd(final String cmd) throws Exception
      {
         shell.execute(cmd);
      }
   }
}
