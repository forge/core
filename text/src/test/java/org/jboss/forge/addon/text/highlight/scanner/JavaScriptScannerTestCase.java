/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.jboss.forge.addon.text.highlight.encoder.NullEncoder;
import org.junit.Ignore;
import org.junit.Test;

public class JavaScriptScannerTestCase extends AbstractScannerTestCase
{

   @Test
   public void shouldMatchJavaScriptEtienneMassipExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "etienne-massip.in.js");
   }

   @Test
   public void shouldMatchJavaScriptGordonExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "gordon.in.js");
   }

   @Test
   public void shouldMatchJavaScriptPrototypeExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "prototype.in.js");
   }

   @Test
   public void shouldMatchJavaScriptReadabilityExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "readability.in.js");
   }

   @Test
   public void shouldMatchJavaScriptScriptAculoUSExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "script.aculo.us.in.js");
   }

   @Test
   public void shouldMatchJavaScriptSunSpiderExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "sun-spider.in.js");
   }

   @Test
   public void shouldMatchJavaScriptTraceTestExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", "trace-test.in.js");
   }

   @Test
   @Ignore
   // known issue http://redmine.rubychan.de/issues/137
   // https://github.com/rubychan/coderay-scanner-tests/blob/master/javascript/xml.known-issue.yaml
   public void shouldMatchJavaScriptXMLExample() throws Exception
   {
      assertMatchExample(Builder.create(), "javascript", " xml.in.js");
   }

   /*
    * JDK 1.7.0_51 -> stable around 85-86 ms JDK 1.8.0 -> stable around 93-104 ms
    */
   @Test
   @Ignore
   // simple Performance setup
   public void performance() throws Exception
   {
      String content = fetch("javascript", "sun-spider.in.js");
      Map<String, Object> options = new HashMap<String, Object>();
      // OutputStream out = NullOutputStream.INSTANCE;
      Encoder encoder = new NullEncoder();
      // Encoder encoder = new TerminalEncoder(out, Syntax.defaultTheme(), new HashMap<String, Object>());

      Scanner scanner = new JavaScriptScanner();
      for (int i = 0; i < 60; i++)
      {
         long start = System.currentTimeMillis();
         scanner.scan(new StringScanner(content), encoder, options);
         System.out.println(i + " [" + (System.currentTimeMillis() - start) + "]");
      }
   }
}