package org.jboss.forge.addon.text.highlight.scanner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.encoder.AssertEncoder;
import org.jboss.forge.furnace.util.Streams;
import org.junit.Assert;

public abstract class AbstractScannerTestCase
{

   private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

   public static final String ASSERT_ENCODER = "TEST";
   {
      Syntax.builtIns();
      Encoder.Factory.registrer(ASSERT_ENCODER, AssertEncoder.class);
   }

   public static String OUTPUT = "examples/";
   public static Pattern MATCH_DATA = Pattern.compile("(.*)\\..*\\..*");

   protected String fetch(String type, String example) throws Exception
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
            Assert.assertEquals("verify line: " + (i + 1), t, s);
         }
      }
      Assert.assertEquals(expectedContent, result);
   }

   static byte[] asByteArray(final InputStream in) throws IllegalArgumentException
   {
      // Precondition check
      if (in == null)
      {
         throw new IllegalArgumentException("stream must be specified");
      }

      // Get content as an array of bytes
      final ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
      final int len = 4096;
      final byte[] buffer = new byte[len];
      int read = 0;
      try
      {
         while (((read = in.read(buffer)) != -1))
         {
            out.write(buffer, 0, read);
         }
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Error in obtainting bytes from " + in, ioe);
      }
      finally
      {
         try
         {
            in.close();
         }
         catch (final IOException ignore)
         {
         }
         // We don't need to close the outstream, it's a byte array out
      }

      // Represent as byte array
      final byte[] content = out.toByteArray();

      // Return
      return content;
   }
}
