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
