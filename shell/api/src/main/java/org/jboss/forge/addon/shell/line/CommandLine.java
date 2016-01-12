/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.line;

import java.util.List;

/**
 * The command line
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandLine
{
   /**
    * @return the arguments for a given command line
    */
   CommandOption getArgument();

   /**
    * @return the parsed options from the command line
    */
   List<CommandOption> getOptions();

   /**
    * @return <code>true</code> if any parameter is set
    */
   default boolean hasParameters()
   {
      return getArgument() != null || getOptions().size() > 0;
   }
}
