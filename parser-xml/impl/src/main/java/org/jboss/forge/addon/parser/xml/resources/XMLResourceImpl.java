/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.xml.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public class XMLResourceImpl extends AbstractFileResource<XMLResource> implements XMLResource
{
   public XMLResourceImpl(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   public XMLResource setContents(Node node)
   {
      setContents(XMLParser.toXMLString(node));
      return this;
   }

   @Override
   public Node getXmlSource() throws FileNotFoundException
   {
      return XMLParser.parse(file);
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new XMLResourceImpl(resourceFactory, file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

}
