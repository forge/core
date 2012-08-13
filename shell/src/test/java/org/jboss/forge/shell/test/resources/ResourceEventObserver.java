/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.resources;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.events.ResourceCreated;
import org.jboss.forge.resources.events.ResourceDeleted;
import org.jboss.forge.resources.events.ResourceModified;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Singleton
public class ResourceEventObserver
{
   private final List<Resource<?>> created = new ArrayList<Resource<?>>();
   private final List<Resource<?>> modified = new ArrayList<Resource<?>>();
   private final List<Resource<?>> deleted = new ArrayList<Resource<?>>();

   void created(@Observes final ResourceCreated event)
   {
      this.created.add(event.getResource());
   }

   void modified(@Observes final ResourceModified event)
   {
      this.modified.add(event.getResource());
   }

   void modified(@Observes final ResourceDeleted event)
   {
      this.deleted.add(event.getResource());
   }

   public List<Resource<?>> getCreated()
   {
      return created;
   }

   public List<Resource<?>> getModified()
   {
      return modified;
   }

   public List<Resource<?>> getDeleted()
   {
      return deleted;
   }
}
