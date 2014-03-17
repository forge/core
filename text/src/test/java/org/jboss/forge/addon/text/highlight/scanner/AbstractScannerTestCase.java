package org.jboss.forge.addon.text.highlight.scanner;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.encoder.AssertEncoder;
import org.junit.Assert;

public abstract class AbstractScannerTestCase {

   public static final String ASSERT_ENCODER = "TEST";
   {
      Encoder.Factory.registrer(ASSERT_ENCODER, AssertEncoder.class);
   }

   public static String OUTPUT = "target/examples";
   public static String BASE_URL = "https://raw.github.com/rubychan/coderay-scanner-tests/master/";
   public static Pattern MATCH_DATA = Pattern.compile("(.*)\\..*\\..*");

   protected String fetch(String type, String example) throws Exception {
      Path sourcePath = Paths.get(OUTPUT, type, example);
      if(!Files.exists(sourcePath)) {
         sourcePath.getParent().toFile().mkdirs();
         URL source = new URL(BASE_URL + type + "/" + example);
         System.out.println("Fetching " + source);
         Files.write(sourcePath, asByteArray(new BufferedInputStream(source.openStream())), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
      }
      return new String(Files.readAllBytes(sourcePath));
   }

   private String expectedName(String example) {
      Matcher result = MATCH_DATA.matcher(example);
      result.find();

      return result.group(1) + ".expected.raydebug";
   }

   protected void assertMatchExample(Syntax.Builder builder, String type, String exampleName) throws Exception {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      String exampleContent = fetch(type, exampleName);
      String expectedContent = fetch(type, expectedName(exampleName));

      builder.output(out);
      builder.encoderType(ASSERT_ENCODER);
      builder.execute(exampleContent);
      out.flush();

      String result = new String(out.toByteArray());

      String[] resultLines = result.split("\n");
      String[] expectedLines = expectedContent.split("\n");

      for(int i = 0; i < resultLines.length; i++) {
         String s = resultLines[i];
         String t = expectedLines[i];

         if(!s.equals(t)) {
            System.out.println("--------------------------->" + (i+1));
            System.out.println(exampleContent.split("\n")[i]);
            System.out.println("---------------------------");
            System.out.println("> " + s);
            System.out.println("< " + t);
            System.out.println("---------------------------");
            Assert.assertEquals("verify line: " + (i+1), t, s);
         }
      }
      Assert.assertEquals(expectedContent, result);
   }

   static byte[] asByteArray(final InputStream in) throws IllegalArgumentException {
      // Precondition check
      if (in == null) {
          throw new IllegalArgumentException("stream must be specified");
      }

      // Get content as an array of bytes
      final ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
      final int len = 4096;
      final byte[] buffer = new byte[len];
      int read = 0;
      try {
          while (((read = in.read(buffer)) != -1)) {
              out.write(buffer, 0, read);
          }
      } catch (final IOException ioe) {
          throw new RuntimeException("Error in obtainting bytes from " + in, ioe);
      } finally {
          try {
              in.close();
          } catch (final IOException ignore) {
          }
          // We don't need to close the outstream, it's a byte array out
      }

      // Represent as byte array
      final byte[] content = out.toByteArray();

      // Return
      return content;
   }
}
