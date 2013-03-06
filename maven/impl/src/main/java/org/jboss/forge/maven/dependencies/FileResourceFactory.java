/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.dependencies;

import java.io.File;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.UnknownFileResource;
import org.jboss.forge.resource.events.ResourceEvent;

/**
 * Simple {@link ResourceFactory} for working outside of a container environment.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class FileResourceFactory implements ResourceFactory
{
   @Override
   @SuppressWarnings("unchecked")
   public <E, T extends Resource<E>> T create(Class<T> type, E underlyingResource)
   {
      return (T) create(underlyingResource);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <E> Resource<E> create(E underlyingResource)
   {
      if (underlyingResource instanceof File)
         return (Resource<E>) new UnknownFileResource(this, (File) underlyingResource);
      return null;
   }

   @Override
   public ResourceFactory fireEvent(ResourceEvent event)
   {
      return this;
   }
}
