package org.jboss.forge.addon.text.highlight.scanner;

import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.junit.Ignore;
import org.junit.Test;

public class XMLScannerTestCase extends AbstractScannerTestCase
{
   @Test
   public void shoulMatchXMLDTDExample() throws Exception
   {
      assertMatchExample(Builder.create(), "xml", "dtd.in.xml");
   }

   @Test
   @Ignore
   // Some issue with new_line in output, revisit
   public void shoulMatchXMLKateExample() throws Exception
   {
      assertMatchExample(Builder.create(), "xml", "kate.in.xml");
   }

   @Test
   public void shoulMatchXMLXAMLExample() throws Exception
   {
      assertMatchExample(Builder.create(), "xml", "xaml.in.xml");
   }
}
