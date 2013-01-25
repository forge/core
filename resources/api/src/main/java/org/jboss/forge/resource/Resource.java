/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.resource;

import java.io.InputStream;
import java.util.List;

import org.jboss.forge.facets.Faceted;

/**
 * A Resource is an abstraction on top of usable items within a Forge project. For instance, files, source code, etc.
 * Like a simplified virtual file system, a Resource is represented hierarchically with a parent and children. This
 * allows addons to say, direct access to project elements within a consistent API from files to class members. </br>
 * However, Resource instances should be treated as representative query result objects. A modification to an instance
 * variable in a resource will not be persisted. Rather than thinking of the Resource object as meta-data (which it is
 * not), it is better conceptualized as a wrapper or "view" of an underlying resource such as a File. For this reason,
 * custom Resource types should never implement any sort of static cache and should preferably lazily initialize their
 * data.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public interface Resource<T> extends Faceted
{

   /**
    * Delete this resource, return true if successful, false if not.
    * 
    * @throws UnsupportedOperationException if deleting is not supported by the underlying implementation.
    */
   boolean delete() throws UnsupportedOperationException;

   /**
    * Delete this resource, return true if successful, false if not.
    * 
    * @param recursive if false and this resource both supports recursive deletion and contains children, deletion will
    *           not occur; otherwise, if true, deletion will propagate to all child resources. Implementations may
    *           choose simply to delegate to {@link #delete()}
    * @throws UnsupportedOperationException if deleting is not supported by the underlying implementation.
    */
   boolean delete(boolean recursive) throws UnsupportedOperationException;

   /**
    * Return the common name of the resource. If it's a file, for instance, just the file name.
    * 
    * @return The name of the resource.
    */
   String getName();

   /**
    * Return the fully qualified name of the resource (if applicable). In the case of a file, this would normally be the
    * full path name.
    * 
    * @return The fully qualified name.
    */
   String getFullyQualifiedName();

   /**
    * Get the parent of the current resource. Returns null if the current resource is the project root.
    * 
    * @return An instance of the resource parent.
    */
   Resource<?> getParent();

   /**
    * Create a new resource instance for the target resource reference of the type that this current resource is.
    * 
    * @param file The target reference to create the resource instance from.
    * @return A new resource.
    */
   Resource<T> createFrom(T file);

   /**
    * Return a list of child resources of the current resource.
    */
   List<Resource<?>> listResources();

   /**
    * Return a list of child resources of the current resource matching the given {@link ResourceFilter}.
    */
   List<Resource<?>> listResources(ResourceFilter filter);

   /**
    * Get the underlying object represented by this {@link Resource}
    */
   T getUnderlyingResourceObject();

   /**
    * Get the {@link InputStream} represented by this {@link Resource}.
    */
   InputStream getResourceInputStream();

   /**
    * Get a child of this resource. Returns null if no child by the given name can be found.
    */
   Resource<?> getChild(String name);

   /**
    * Return true if this {@link Resource} exists, return false if not.
    */
   boolean exists();

   /**
    * Ask this {@link Resource} if it is actually a resource of the given type; if it is, return a new reference to the
    * resource as the given type, otherwise return null.
    */
   <R extends Resource<?>> R reify(final Class<R> type);

   /**
    * Return the {@link ResourceFactory} with which this {@link Resource} was created. If no factory was used, return
    * null.
    */
   ResourceFactory getResourceFactory();
}
