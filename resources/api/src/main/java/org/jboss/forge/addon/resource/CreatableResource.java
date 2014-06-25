/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

/**
 * A {@link CreatableResource} allows its underlying resource to be created.
 * 
 * @param <T> The {@link Resource} type that implements this interface.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CreatableResource<T extends CreatableResource<T, R>, R> extends Resource<R>
{
   /**
    * Create this {@link Resource} in the underlying resource system. Necessary parent paths will be created
    * automatically.
    */
   boolean create();

}