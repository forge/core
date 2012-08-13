/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.Queue;

import javax.inject.Inject;

import org.jboss.forge.shell.command.parser.Tokenizer;
import org.jboss.forge.shell.spi.CommandInterceptor;

/**
 * Responsible for converting aliases into full commands.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AliasInterceptor implements CommandInterceptor
{
   @Inject
   private AliasRegistry registry;

   @Override
   public String intercept(String line)
   {
      Queue<String> tokens = new Tokenizer().tokenize(line);
      String first = tokens.peek();

      if (registry.hasAlias(first))
      {
         line = line.replaceFirst(first, registry.getAlias(first));
      }

      return line;
   }
}
