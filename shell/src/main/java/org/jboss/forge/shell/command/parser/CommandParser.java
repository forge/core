/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.Queue;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CommandParser
{
   /**
    * Assuming the given {@link CommandMetadata} has already been determined, use the remaining tokens to parse as many
    * tokens as possible from the given {@link Queue}.
    * 
    * @return a map of options mapped to their given values.
    */
   public CommandParserContext parse(CommandMetadata command, Queue<String> tokens, CommandParserContext context);
}
