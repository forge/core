/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:rdruss@gmail.com">Rodney Russ</a>
 */
@Alias("pwr")
@Topic("File & Resources")
@Help("Prints the current working resource.")
public class PwrPlugin implements org.jboss.forge.shell.plugins.Plugin
{

   @Inject
   private Shell shell;

   @DefaultCommand
   public void run()
   {
      shell.println(shell.getCurrentResource().getFullyQualifiedName());
   }
}
