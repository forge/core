/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.encoder.AssertEncoder;
import org.jboss.forge.furnace.util.Streams;

import static org.junit.Assert.assertEquals;

public abstract class AbstractScannerTestCase
{

   private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

   static final String ASSERT_ENCODER = "TEST";

   static
   {
      Syntax.builtIns();
      Encoder.Factory.registrer(ASSERT_ENCODER, AssertEncoder.class);
   }

   public static String OUTPUT = "examples/";
   public static Pattern MATCH_DATA = Pattern.compile("(.*)\\..*\\..*");

   protected String fetch(String type, String example)
   {
      InputStream is = getClass().getClassLoader().getResourceAsStream(OUTPUT + type + "/" + example);
      return Streams.toString(is, DEFAULT_CHARSET);
   }

   private String expectedName(String example)
   {
      Matcher result = MATCH_DATA.matcher(example);
      result.find();

      return result.group(1) + ".expected.raydebug";
   }

   protected void assertMatchExample(Syntax.Builder builder, String type, String exampleName) throws Exception
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      String exampleContent = fetch(type, exampleName);
      String expectedContent = fetch(type, expectedName(exampleName));

      builder.output(out);
      builder.encoderType(ASSERT_ENCODER);
      builder.scanner(Scanner.Factory.byFileName(exampleName));
      builder.execute(exampleContent);
      out.flush();

      String result = new String(out.toByteArray());

      String[] resultLines = result.split("\n");
      String[] expectedLines = expectedContent.split("\n");

      for (int i = 0; i < resultLines.length; i++)
      {
         String s = resultLines[i];
         String t = expectedLines[i];

         if (!s.equals(t))
         {
            System.out.println("--------------------------->" + (i + 1));
            System.out.println(exampleContent.split("\n")[i]);
            System.out.println("---------------------------");
            System.out.println("> " + s);
            System.out.println("< " + t);
            System.out.println("---------------------------");
         }
         assertEquals("verify line " + (i + 1) + " for " + type + "/" + exampleName, t, s);
      }
      assertEquals(expectedContent, result);
   }
}
