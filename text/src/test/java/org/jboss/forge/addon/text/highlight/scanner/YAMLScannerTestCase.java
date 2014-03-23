package org.jboss.forge.addon.text.highlight.scanner;

import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.junit.Test;

public class YAMLScannerTestCase extends AbstractScannerTestCase {

   @Test
   public void shoulMatchYAMLBasicExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "basic.in.yml");
   }

   @Test
   public void shoulMatchYAMLDatabaseExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "database.in.yml");
   }

   @Test
   public void shoulMatchYAMLFAQExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "faq.in.yml");
   }

   @Test
   public void shoulMatchYAMLGemspecExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "gemspec.in.yml");
   }

   @Test
   public void shoulMatchYAMLLatexEntitiesExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "latex_entities.in.yml");
   }

   @Test
   public void shoulMatchYAMLMultilineExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "multiline.in.yml");
   }

   @Test
   public void shoulMatchYAMLProblemExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "problem.in.yml");
   }

   @Test
   public void shoulMatchYAMLThresholdExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "threshold.in.yml");
   }

   @Test
   public void shoulMatchYAMLWebsiteExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "website.in.yml");
   }
}
