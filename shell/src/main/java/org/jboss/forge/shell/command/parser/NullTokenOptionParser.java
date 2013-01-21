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
 * Used at the end of the {@link CommandParser} chain to signal with an {@link IllegalStateException} that an invalid
 * token was encountered.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NullTokenOptionParser implements CommandParser
{

   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      String token = tokens.remove();
      ctx.addWarning("Swallowed unknown token [" + token + "] for command [" + command + "].");
      ctx.addIgnoredToken(token);
      return ctx;
   }

}
