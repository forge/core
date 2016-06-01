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

public class HTMLScannerTestCase extends AbstractScannerTestCase
{

   @Test
   @Ignore
   // simple developer test
   public void should() throws Exception
   {

      String source = "<p style=\"float:right;\">#{q.answers.size.to_i} answers</p>";

      Syntax.Builder.create().scannerType(HTMLScanner.TYPE.getName()).encoderType(ASSERT_ENCODER).execute(source);

      assertTextToken(TokenType.tag, "<p");
      assertTextToken(TokenType.attribute_name, "style");
      assertTextToken(TokenType.key, "float");
      // assertTextToken(TokenType.tag, "<html>", "<head>", "<meta", "<title>", "<body>", "<link", "<style>", "<script",
      // "<div", "<hr>", "<footer>");
      // assertTextToken(TokenType.attribute_name, "charset", "content", "src", "class");
      // assertTextToken(TokenType.content, "utf-8", "navbar-inner", "text/javascript",
      // "width=device-width, initial-scale=1.0");
   }

   @Test
   public void shouldMatchHTMLBooleanExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "boolean.in.html");
   }

   @Test
   public void shouldMatchHTMLAmpersandExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "ampersand.in.html");
   }

   @Test
   public void shouldMatchHTMLCDataExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "cdata.in.html");
   }

   @Test
   public void shouldMatchHTMLCoderayOutputExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "coderay-output.in.html");
   }

   @Test
   public void shouldMatchHTMLRedmineExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "redmine.in.html");
   }

   @Test
   public void shouldMatchHTMLTagsExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "tags.in.html");
   }

   @Test
   public void shouldMatchHTMLTolkienTagsExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "tolkien.in.html");
   }

   @Test
   @Ignore
   public void shouldMatchHTMLTPuthTagsExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "tputh.in.html");
   }
}
