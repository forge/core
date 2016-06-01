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

public class JavaScannerTestCase extends AbstractScannerTestCase
{

   @Test
   @Ignore
   // simple developer test
   public void should() throws Exception
   {

      String source = "/***** BEGIN LICENSE BLOCK ***** */\n"
               +
               "package pl.silvermedia.ws;\n"
               +
               "import java.util.List;\n"
               +
               "\n"
               +
               "import javax.jws.WebParam;\n"
               +
               "import javax.jws.WebService;\n"
               +
               "\n"
               +
               "@WebService\n"
               +
               "public interface ContactUsService {\n"
               +
               "  List<Message> getMessages();\n"
               +
               "  Message[] getFirstMessage();\n"
               +
               "    void postMessage(@WebParam(name = \"message\") Message message) throws UnsupportedOperationException {\n"
               +
               "        if (File.separatorChar == '\\\\') {" +
               "            bannerText = \"  \" + bannerText + \"  \\n\\n\";\n" +
               "        }\n" +
               "    }" +
               "}\n" +
               "";

      Syntax.Builder.create().scannerType(JavaScanner.TYPE.getName()).encoderType(ASSERT_ENCODER).execute(source);

      assertTextToken(TokenType.comment, "/***** BEGIN LICENSE BLOCK ***** */");
      assertTextToken(TokenType.namespace, "pl.silvermedia.ws");
      assertTextToken(TokenType.predefined_type, "List");
      assertTextToken(TokenType.exception, "UnsupportedOperationException");
      assertTextToken(TokenType.keyword, "import");
      assertTextToken(TokenType.type, "void", "interface", "[]");
      assertTextToken(TokenType.directive, "public");
      assertTextToken(TokenType.content, "message");
      assertTextToken(TokenType.char_, "\\n", "\\\\");
   }

   @Test
   public void shouldMatchJavaExample() throws Exception
   {
      assertMatchExample(Builder.create(), "java", "example.in.java");
   }

   @Test
   public void shouldMatchJavaJRubyExample() throws Exception
   {
      assertMatchExample(Builder.create(), "java", "jruby.in.java");
   }
}
