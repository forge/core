/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.project.services.ResourceFactory;

/**
 * Represents any regular file which Forge does not hav any special handler for.
 * 
 * @author Mike Brock
 */
public class UnknownFileResource extends FileResource<UnknownFileResource>
{
   public UnknownFileResource(final ResourceFactory factory)
   {
      super(factory, null);
      setFlag(ResourceFlag.Leaf);
   }

   public UnknownFileResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public UnknownFileResource createFrom(final File file)
   {
      return new UnknownFileResource(resourceFactory, file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public String toString()
   {
      return file.getName();
   }
}
