/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project.resources;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.events.ResourceChanged;

/**
 * Contains the current {@link Resource} - not to be used by outsiders.
 * 
 * @author Mike Brock <cbrock@redhat.com>
 */
@Singleton
public class CurrentResource
{
   // FIXME Resource API needs to be separated from project API
   private Resource<?> current;

   @Inject
   private Event<ResourceChanged> event;

   public Resource<?> getCurrent()
   {
      return current;
   }

   public void setCurrent(final Resource<?> newResource)
   {
      ResourceChanged resourceChanged = new ResourceChanged(current, newResource);
      this.current = newResource;
      event.fire(resourceChanged);
   }

   @Override
   public String toString()
   {
      return "ResourceContext [" + current + "]";
   }

}
