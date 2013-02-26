/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.resource.transaction.ChangeSet;

public class ChangeSetImpl implements ChangeSet
{
   private Set<Resource<?>> changeSet = new HashSet<Resource<?>>();

   public ChangeSetImpl()
   {
   }

   public void addResource(Resource<?> resource)
   {
      changeSet.add(resource);
   }

   public void removeResource(Resource<?> resource)
   {
      changeSet.remove(resource);
   }

   @Override
   public Set<Resource<?>> getModifiedResources()
   {
      return changeSet;
   }

}
