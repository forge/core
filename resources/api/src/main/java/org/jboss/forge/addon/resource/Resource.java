/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.resource.util.ResourcePathResolver;
import org.jboss.forge.furnace.addons.Addon;

/**
 * A {@link Resource} is an abstraction on top of usable items within a Furnace project. For instance, files, source
 * code, etc. Like a simplified virtual file system, a Resource is represented hierarchically with a parent and
 * children. This allows {@link Addon} instances to, for example, directly access to project elements within a
 * consistent API from files to class members.
 * <p>
 * However, resource instances should be treated as representative query result objects. A modification to an instance
 * variable in a resource will not be persisted. Rather than thinking of the resource object as meta-data (which it is
 * not), it is better conceptualized as a wrapper or "view" of an underlying resource such as a {@link File}. For this
 * reason, custom resource types should never implement any sort of static cache and should preferably lazily initialize
 * their data.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public interface Resource<T> extends Faceted<ResourceFacet>
{

   /**
    * Delete this {@link Resource}, return <code>true</code> if successful, <code>false</code> if not.
    * 
    * @throws UnsupportedOperationException if deleting is not supported by the underlying implementation.
    */
   boolean delete() throws UnsupportedOperationException;

   /**
    * Delete this resource, return <code>true</code> if successful, <code>false</code> if not.
    * 
    * @param recursive if false and this resource both supports recursive deletion and contains children, deletion will
    *           not occur; otherwise, if true, deletion will propagate to all child resources. Implementations may
    *           choose simply to delegate to {@link #delete()}
    * @throws UnsupportedOperationException if deleting is not supported by the underlying implementation.
    */
   boolean delete(boolean recursive) throws UnsupportedOperationException;

   /**
    * Return the common name of the {@link Resource}. If it's a file, for instance, just the file name.
    * 
    * @return The name of the {@link Resource}.
    */
   String getName();

   /**
    * Return the fully qualified name of the resource (if applicable). In the case of a {@link File} resource, this
    * would normally be the full path name.
    * 
    * @return The fully qualified name.
    */
   String getFullyQualifiedName();

   /**
    * Get the parent of the current resource. Returns <code>null</code> if the current resource is the filesystem root.
    * 
    * @return An instance of the {@link Resource} parent.
    */
   Resource<?> getParent();

   /**
    * Create a new resource instance for the target resource reference of the type that this current resource is.
    * 
    * @param file The target reference to create the resource instance from.
    * @return A new {@link Resource} instance.
    */
   Resource<T> createFrom(T file);

   /**
    * Return a list of child resources of the current resource. (Never null.)
    */
   List<Resource<?>> listResources();

   /**
    * Return a list of child resources of the current resource matching the given {@link ResourceFilter}.
    */
   List<Resource<?>> listResources(ResourceFilter filter);

   /**
    * Get the underlying object represented by this {@link Resource}.
    */
   T getUnderlyingResourceObject();

   /**
    * Get the {@link InputStream} represented by this {@link Resource}.
    */
   InputStream getResourceInputStream();

   /**
    * Get the entire contents of this {@link Resource}, returned as a {@link String} using UTF-8 encoding.
    */
   String getContents();

   /**
    * Get the entire contents of this {@link Resource}, returned as a {@link String} using the specified encoding.
    */
   String getContents(Charset charset);

   /**
    * Get a child of this resource. Returns <code>null</code> if no child by the given name can be found.
    */
   Resource<?> getChild(String name);

   /**
    * Return <code>true</code> if this {@link Resource} exists, return <code>false</code> if not.
    */
   boolean exists();

   /**
    * Ask this {@link Resource} if it is actually a resource of the given type; if it is, return a new reference to the
    * resource as the given type, otherwise return <code>null</code>.
    */
   <R extends Resource<?>> R reify(final Class<R> type);

   /**
    * Ask this {@link Resource} if it is actually a resource of the given type; if it is, return a new reference to the
    * resource as the given type, otherwise return <code>null</code>.
    */
   default <R extends Resource<?>> R as(final Class<R> type) {
      return reify(type);
   }
   /**
    * Return the {@link ResourceFactory} with which this {@link Resource} was created. If no factory was used, return
    * <code>null</code>.
    */
   ResourceFactory getResourceFactory();

   /**
    * Resolve children resources from this {@link Resource} given a specific path.
    * 
    * @see ResourcePathResolver#resolve()
    */
   List<Resource<?>> resolveChildren(String path);
}
