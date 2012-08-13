/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.resources.Resource;

/**
 * An event that notifies observers immediately after the current {@link Resource} has been changed to a new
 * {@link Resource}.
 * <p>
 * <strong>For example:</strong>
 * <p>
 * <code>public void myObserver(@Observes {@link ResourceChanged} event)<br/>
 * {<br/>
 *    // do something<br/>
 * }<br/>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class ResourceChanged
{
   private final Resource<?> oldResource;
   private final Resource<?> newResource;

   public ResourceChanged(final Resource<?> oldResource, final Resource<?> newResource)
   {
      this.oldResource = oldResource;
      this.newResource = newResource;
   }

   /**
    * @return the old {@link Resource}
    */
   public Resource<?> getOldResource()
   {
      return oldResource;
   }

   /**
    * @return the new {@link Resource}
    */
   public Resource<?> getNewResource()
   {
      return newResource;
   }
}
