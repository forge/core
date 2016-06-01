/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import static org.jboss.forge.addon.text.highlight.encoder.AssertEncoder.assertTextToken;

import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.junit.Ignore;
import org.junit.Test;

public class JSONScannerTestCase extends AbstractScannerTestCase
{

   @Test
   @Ignore
   // simple developer test
   public void should() throws Exception
   {

      String source = "[\n" +
               "   {\n" +
               "      \"precision\": \"zip\",\n" +
               "      \"Latitude\":  37,\n" +
               "      \"Longitude\": -122.3959,\n" +
               "      \"Address\":   \"\",\n" +
               "      \"City\":      \"SAN FRANCISCO\",\n" +
               "      \"State\":     \"CA\",\n" +
               "      \"Zip\":       \"94107\",\n" +
               "      \"Country\":   \"US\"\n" +
               "   },\n" +
               "   {\n" +
               "      \"precision\": \"zip\",\n" +
               "      \"Latitude\":  37.371991,\n" +
               "      \"Longitude\": -122.026020,\n" +
               "      \"Address\":   \"\",\n" +
               "      \"City\":      \"SUNNYVALE\",\n" +
               "      \"State\":     \"CA\",\n" +
               "      \"Zip\":       \"94085\",\n" +
               "      \"Country\":   \"US\"\n" +
               "   }\n" +
               "]\n";

      Syntax.Builder.create().scannerType(JSONScanner.TYPE.getName()).encoderType(ASSERT_ENCODER).execute(source);

      assertTextToken(TokenType.content, "Zip", "precision");
      assertTextToken(TokenType.content, "zip", "CA", "US");
      assertTextToken(TokenType.integer, "37");
      assertTextToken(TokenType.float_, "37.371991", "-122.3959");
   }

   @Test
   public void shouldMatchJSONExample() throws Exception
   {
      assertMatchExample(Builder.create(), "json", "example.in.json");
   }

   @Test
   public void shouldMatchJSONLibExample() throws Exception
   {
      assertMatchExample(Builder.create(), "json", "json-lib.in.json");
   }

   @Test
   public void shouldMatchJSONBigExample() throws Exception
   {
      assertMatchExample(Builder.create(), "json", "big.in.json");
   }

   @Test
   public void shouldMatchJSONBig2Example() throws Exception
   {
      assertMatchExample(Builder.create(), "json", "big2.in.json");
   }
}
