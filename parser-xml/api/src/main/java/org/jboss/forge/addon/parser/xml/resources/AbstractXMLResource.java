/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
 * An abstract class for {@link XMLResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractXMLResource extends AbstractFileResource<XMLResource>implements XMLResource
{
   public AbstractXMLResource(final ResourceFactory factory, final File file)
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
      return XMLParser.parse(getResourceInputStream());
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

}
