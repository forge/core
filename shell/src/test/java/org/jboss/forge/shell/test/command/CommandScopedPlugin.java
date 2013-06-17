/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("cmdscope")
public class CommandScopedPlugin implements Plugin
{

   @Inject
   Instance<CommandScopedObject> cmdScopedObj;

   @DefaultCommand
   public void testMock(PipeOut out)
   {
      int value = cmdScopedObj.get().getValue();
      out.println("" + value);
   }
}
