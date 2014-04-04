package org.jboss.forge.addon.text.highlight;

import org.jboss.aesh.extensions.text.highlight.Encoder;
import org.jboss.aesh.extensions.text.highlight.Scanner;
import org.jboss.aesh.extensions.text.highlight.StringScanner;
import org.jboss.aesh.extensions.text.highlight.TokenType;

import java.util.Map;
import java.util.UUID;

public class TestScanner implements Scanner {

   public static final String TEST_STRING = UUID.randomUUID().toString();
   public static Type TYPE = new Type("TEST", "\\.(test)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options) {
      encoder.textToken(TEST_STRING, TokenType.string);
   }
}
