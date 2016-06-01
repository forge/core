/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.junit.Test;

public class YAMLScannerTestCase extends AbstractScannerTestCase {

   @Test
   public void shouldMatchYAMLBasicExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "basic.in.yml");
   }

   @Test
   public void shouldMatchYAMLDatabaseExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "database.in.yml");
   }

   @Test
   public void shouldMatchYAMLFAQExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "faq.in.yml");
   }

   @Test
   public void shouldMatchYAMLGemspecExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "gemspec.in.yml");
   }

   @Test
   public void shouldMatchYAMLLatexEntitiesExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "latex_entities.in.yml");
   }

   @Test
   public void shouldMatchYAMLMultilineExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "multiline.in.yml");
   }

   @Test
   public void shouldMatchYAMLProblemExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "problem.in.yml");
   }

   @Test
   public void shouldMatchYAMLThresholdExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "threshold.in.yml");
   }

   @Test
   public void shouldMatchYAMLWebsiteExample() throws Exception
   {
      assertMatchExample(Builder.create(), "yaml", "website.in.yml");
   }
}
