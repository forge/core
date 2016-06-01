/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;


/**
 * A standard, build-in, resource for representing directories on the file-system.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DirectoryResource extends FileResource<DirectoryResource>
{
   /**
    * Obtain a reference to the child {@link DirectoryResource}. If that resource does not exist, return a new instance.
    * If the resource exists and is not a {@link DirectoryResource}, throw {@link ResourceException}
    */
   public DirectoryResource getChildDirectory(final String name) throws ResourceException;

   /**
    * Obtain a reference to the child {@link DirectoryResource}. If that resource does not exist, return a new instance
    * and attempt to create the a directory of the given name. If the resource exists and is not a
    * {@link DirectoryResource}, throw {@link ResourceException}
    */
   public DirectoryResource getOrCreateChildDirectory(String name);

   /**
    * Using the given type, obtain a reference to the child resource of the given type. If the result is not of the
    * requested type and does not exist, return null. If the result is not of the requested type and exists, throw
    * {@link ResourceException}
    */
   public <E, T extends Resource<E>> T getChildOfType(final Class<T> type, final String name) throws ResourceException;

}
