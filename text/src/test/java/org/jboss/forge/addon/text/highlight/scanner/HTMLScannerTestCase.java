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
   public void shoulMatchHTMLBooleanExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "boolean.in.html");
   }

   @Test
   public void shoulMatchHTMLAmpersandExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "ampersand.in.html");
   }

   @Test
   public void shoulMatchHTMLCDataExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "cdata.in.html");
   }

   @Test
   public void shoulMatchHTMLCoderayOutputExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "coderay-output.in.html");
   }

   @Test
   public void shoulMatchHTMLRedmineExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "redmine.in.html");
   }

   @Test
   public void shoulMatchHTMLTagsExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "tags.in.html");
   }

   @Test
   public void shoulMatchHTMLTolkienTagsExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "tolkien.in.html");
   }

   @Test
   public void shoulMatchHTMLTPuthTagsExample() throws Exception
   {
      assertMatchExample(Builder.create(), "html", "tputh.in.html");
   }
}
