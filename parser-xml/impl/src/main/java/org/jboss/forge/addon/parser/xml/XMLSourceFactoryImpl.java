/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public class XMLSourceFactoryImpl implements XMLSourceFactory
{
   /**
    * Open the given {@link File}, parsing its contents into a new {@link Node} instance.
    */
   @Override
   public Node parse(final File file) throws FileNotFoundException
   {
      return XMLParser.parse(file);
   }

   /**
    * Read the given {@link InputStream} and parse the data into a new {@link Node} instance.
    */
   @Override
   public Node parse(final InputStream data)
   {
      return XMLParser.parse(data);
   }

   /**
    * Parse the given character array into a new {@link Node} instance.
    */
   @Override
   public Node parse(final char[] data)
   {
      return XMLParser.parse(new String(data));
   }

   /**
    * Parse the given String data into a new {@link Node} instance.
    */
   @Override
   public Node parse(final String data)
   {
      return XMLParser.parse(data);
   }

}
