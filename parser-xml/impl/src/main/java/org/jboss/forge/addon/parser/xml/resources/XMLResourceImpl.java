/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml.resources;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public class XMLResourceImpl extends AbstractXMLResource
{
   public XMLResourceImpl(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new XMLResourceImpl(getResourceFactory(), file);
   }
}
