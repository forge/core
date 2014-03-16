package org.jboss.forge.addon.text.highlight.scanner;

import static org.jboss.forge.addon.text.highlight.encoder.AssertEncoder.assertTextToken;

import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.junit.Ignore;
import org.junit.Test;

public class HTMLScannerTestCase extends AbstractScannerTestCase {

   @Test @Ignore // simple developer test
   public void should() throws Exception {

      String source = "<p style=\"float:right;\">#{q.answers.size.to_i} answers</p>";

      Syntax.Builder.create().scannerType(Scanner.Type.HTML).encoderType(ASSERT_ENCODER).execute(source);

      assertTextToken(TokenType.tag, "<p");
      assertTextToken(TokenType.attribute_name, "style");
      assertTextToken(TokenType.key, "float");
      //assertTextToken(TokenType.tag, "<html>", "<head>", "<meta", "<title>", "<body>", "<link", "<style>", "<script", "<div", "<hr>", "<footer>");
      //assertTextToken(TokenType.attribute_name, "charset", "content", "src", "class");
      //assertTextToken(TokenType.content, "utf-8", "navbar-inner", "text/javascript", "width=device-width, initial-scale=1.0");
   }

   @Test
   public void shoulMatchHTMLBooleanExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "boolean.in.html");
   }

   @Test
   public void shoulMatchHTMLAmpersandExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "ampersand.in.html");
   }

   @Test
   public void shoulMatchHTMLCDataExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "cdata.in.html");
   }

   @Test
   public void shoulMatchHTMLCoderayOutputExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "coderay-output.in.html");
   }

   @Test
   public void shoulMatchHTMLRedmineExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "redmine.in.html");
   }

   @Test
   public void shoulMatchHTMLTagsExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "tags.in.html");
   }

   @Test
   public void shoulMatchHTMLTolkienTagsExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "tolkien.in.html");
   }

   @Test
   public void shoulMatchHTMLTPuthTagsExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "html", "tputh.in.html");
   }

   @Test
   public void shoulMatchXMLDTDExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "xml", "dtd.in.xml");
   }

   @Test @Ignore // Some issue with new_line in output, revisit
   public void shoulMatchXMLKateExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "xml", "kate.in.xml");
   }

   @Test
   public void shoulMatchXMLXAMLExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.HTML), "xml", "xaml.in.xml");
   }
}
