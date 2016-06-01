/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.junit.Ignore;
import org.junit.Test;

public class XMLScannerTestCase extends AbstractScannerTestCase
{
   @Test
   public void shouldMatchXMLDTDExample() throws Exception
   {
      assertMatchExample(Builder.create(), "xml", "dtd.in.xml");
   }

   @Test
   @Ignore
   // Some issue with new_line in output, revisit
   public void shouldMatchXMLKateExample() throws Exception
   {
      assertMatchExample(Builder.create(), "xml", "kate.in.xml");
   }

   @Test
   public void shouldMatchXMLXAMLExample() throws Exception
   {
      assertMatchExample(Builder.create(), "xml", "xaml.in.xml");
   }
}
