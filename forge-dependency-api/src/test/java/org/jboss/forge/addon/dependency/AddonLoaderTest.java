/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.forge.parser.xml.XMLParser;
import org.junit.Assert;
import org.junit.Test;

public class AddonLoaderTest
{
   private AddonLoader loader = new AddonLoader();

   @Test
   public void testAddonLoad() throws IOException
   {
      InputStream stream = getClass().getResourceAsStream("installed.xml");
      loader.load(XMLParser.parse(stream));
      Assert.assertEquals(2, loader.getCoordinates().size());
   }
}
