/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.json.resource;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;

public class JsonResourceImpl extends AbstractJsonResource implements JsonResource
{
   public JsonResourceImpl(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new JsonResourceImpl(getResourceFactory(), file);
   }
}
