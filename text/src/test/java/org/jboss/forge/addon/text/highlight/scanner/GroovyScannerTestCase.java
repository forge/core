package org.jboss.forge.addon.text.highlight.scanner;

import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.junit.Test;

public class GroovyScannerTestCase extends AbstractScannerTestCase {

   @Test
   public void shouldMatchGroovyPleacExample() throws Exception {
      assertMatchExample(Builder.create(), "groovy", "pleac.in.groovy");
   }

   @Test
   public void shouldMatchGroovyRaistlin77Example() throws Exception {
      assertMatchExample(Builder.create(), "groovy", "raistlin77.in.groovy");
   }

   @Test
   public void shouldMatchGroovyStrangeExample() throws Exception {
      assertMatchExample(Builder.create(), "groovy", "strange.in.groovy");
   }

   @Test
   public void shouldMatchGroovyStringsExample() throws Exception {
      assertMatchExample(Builder.create(), "groovy", "strings.in.groovy");
   }
}
