/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource;

import java.io.File;
import java.io.IOException;

import org.jboss.forge.resource.transaction.ChangeSet;
import org.jboss.forge.resource.transaction.ResourceTransaction;

public class ResourceTransactionImpl implements ResourceTransaction
{
   private File tempWorkspace;

   private ChangeSetImpl changeSet = new ChangeSetImpl();
   private ResourceFactoryImpl factoryImpl;

   public ResourceTransactionImpl(ResourceFactoryImpl factoryImpl) throws ResourceException
   {
      try
      {
         tempWorkspace = File.createTempFile("Forge_RT", null);
      }
      catch (IOException io)
      {
         throw new ResourceException(io);
      }
      tempWorkspace.delete();
      tempWorkspace.mkdir();
      this.factoryImpl = factoryImpl;
   }

   @Override
   public void commit() throws ResourceException
   {
      factoryImpl.unsetTransaction();
   }

   @Override
   public void rollback() throws ResourceException
   {
      factoryImpl.unsetTransaction();
   }

   /**
    * Decorates a Resource
    *
    * @param resource
    * @return
    */
   public <TYPE> Resource<TYPE> decorateResource(Resource<TYPE> resource)
   {
      changeSet.addResource(resource);
      // TODO: Create a proxy to the FileResource
      return resource;
   }

   @Override
   public ChangeSet getChangeSet()
   {
      return changeSet;
   }

}
