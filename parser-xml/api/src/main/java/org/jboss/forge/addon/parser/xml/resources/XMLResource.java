/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml.resources;

import java.io.FileNotFoundException;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.parser.xml.Node;

/**
 * A {@link Resource} that represents a XML {@link Node}.
 * 
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public interface XMLResource extends FileResource<XMLResource>
{
   /**
    * Set the content of this {@link Resource} to the value of the given {@link XMLSource}.
    */
   public XMLResource setContents(final Node source);

   /**
    * Attempt to determine and return the {@link XMLSource} type of the underlying {@link Class}.
    */
   public Node getXmlSource() throws FileNotFoundException;

}
