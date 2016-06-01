/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

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
