/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.TokenType;

public class PlainScanner implements Scanner
{
   private static final Pattern ALL = Pattern.compile(".*", Pattern.DOTALL);

   // Never match a File, only match by default if no one else does. Handled in Scanner.Factory
   public static final Type TYPE = new Type("PLAIN", (Pattern)null);

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {
      MatchResult m = source.scan(ALL);
      if (m != null)
      {
         encoder.textToken(m.group(), TokenType.plain);
      }
   }

}
