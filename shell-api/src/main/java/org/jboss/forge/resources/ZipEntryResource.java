/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.forge.project.services.ResourceFactory;

/**
 * Represents an entry in zip archive.
 * 
 * @author Adolfo Junior
 */
public class ZipEntryResource extends AbstractResource<String>
{
   private String name;

   private ZipResource zip;

   private List<Resource<?>> children;

   /**
    * Constructor for ROOT entry.
    */
   protected ZipEntryResource(ResourceFactory factory, ZipResource parent)
   {
      this(factory, parent, parent, "");
   }

   /**
    * Constructor for an CHILD entry.
    */
   protected ZipEntryResource(ResourceFactory factory, ZipResource zip, Resource<?> parent, String name)
   {
      super(factory, parent);
      this.zip = zip;
      this.name = name;
   }

   @Override
   public boolean exists()
   {
      return true;
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Used to create a child entry from this owner. Because this dont have {@link ResourceHandles}, this will never be
    * called from {@link ResourceFactory}.
    */
   @Override
   public ZipEntryResource createFrom(String name)
   {
      return new ZipEntryResource(resourceFactory, zip, this, name);
   }

   public synchronized List<Resource<?>> listResources(final ResourceFilter filter, boolean recursively)
   {
      if (filter == null)
      {
         return doListResources(recursively);
      }
      else
      {
         List<Resource<?>> result = new ArrayList<Resource<?>>();

         for (Resource<?> resource : doListResources(recursively))
         {
            if (filter.accept(resource))
            {
               result.add(resource);
            }
         }

         Collections.sort(result, new FullyQualifiedNameComparator());

         return result;
      }
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return doListResources(false);
   }

   protected List<Resource<?>> doListResources(boolean recursively)
   {
      List<Resource<?>> list = null;

      if (recursively)
      {
         doListRecursively(list = new LinkedList<Resource<?>>());
      }
      else
      {
         list = children;
      }

      return list == null ? Collections.<Resource<?>> emptyList() : list;
   }

   protected void doListRecursively(List<Resource<?>> list)
   {
      if (children != null)
      {
         for (Resource<?> child : children)
         {
            list.add(child);
            ((ZipEntryResource) child).doListRecursively(list);
         }
      }
   }

   @Override
   public String toString()
   {
      return getFullyQualifiedName();
   }

   @Override
   public String getName()
   {
      return name;
   }

   /**
    * Relative path to the zip!
    */
   @Override
   public String getFullyQualifiedName()
   {
      String entryName = getName();
      Resource<?> parent = getParent();
      if (parent instanceof ZipEntryResource)
      {
         entryName = parent.getFullyQualifiedName() + "/" + entryName;
      }
      return entryName;
   }

   @Override
   public ZipEntryResource getChild(String name)
   {
      for (Resource<?> resource : doListResources())
      {
         if (resource.getName().equals(name))
         {
            return (ZipEntryResource) resource;
         }
      }
      return null;
   }

   protected ZipEntryResource getChildOrCreate(String name)
   {
      ZipEntryResource child = getChild(name);

      if (child == null)
      {
         if (children == null)
         {
            children = new LinkedList<Resource<?>>();
         }
         children.add(child = createFrom(name));
         // update to node, because this have children!
         setFlag(ResourceFlag.Node);
      }
      return child;
   }

   /**
    * The Underlying Object from an Entry is the only the name.
    */
   @Override
   public String getUnderlyingResourceObject()
   {
      return name;
   }

   @Override
   public InputStream getResourceInputStream()
   {
      String fullyQualifiedName = getFullyQualifiedName();
      if (!fullyQualifiedName.isEmpty())
      {
         return new ZipEntryInputStream(zip.getFile(), fullyQualifiedName);
      }
      return null;
   }

   /**
    * If parent is ROOT, then return {@link ZipResource2}
    */
   @Override
   public Resource<?> getParent()
   {
      Resource<?> parent = super.getParent();
      if (zip != null && zip.getRootEntry() == parent)
      {
         return zip;
      }
      return parent;
   }

   protected static class FullyQualifiedNameComparator implements Comparator<Resource<?>>
   {
      @Override
      public int compare(Resource<?> left, Resource<?> right)
      {
         return left.getFullyQualifiedName().compareTo(right.getFullyQualifiedName());
      }
   }

   /**
    * Lazy ZipEntry input stream.
    * 
    * @author Adolfo Junior
    */
   private static class ZipEntryInputStream extends InputStream
   {
      private File file;

      private String entry;

      private ZipFile zip;

      private InputStream delegate;

      private boolean closed;

      public ZipEntryInputStream(File file, String entry)
      {
         this.file = file;
         this.entry = entry;
      }

      private void ensureOpen() throws IOException
      {
         if (closed)
         {
            throw new ZipException("ZipFile closed");
         }
         if (delegate == null)
         {
            zip = new ZipFile(file);
            delegate = zip.getInputStream(zip.getEntry(entry));
         }
      }

      @Override
      public long skip(long n) throws IOException
      {
         ensureOpen();
         return delegate.skip(n);
      }

      @Override
      public synchronized void mark(int readlimit)
      {
         delegate.mark(readlimit);
      }

      @Override
      public boolean markSupported()
      {
         return delegate.markSupported();
      }

      @Override
      public synchronized void reset() throws IOException
      {
         ensureOpen();
         delegate.reset();
      }

      @Override
      public int available() throws IOException
      {
         ensureOpen();
         return delegate.available();
      }

      @Override
      public int read() throws IOException
      {
         ensureOpen();
         return delegate.read();
      }

      @Override
      public int read(byte[] b) throws IOException
      {
         ensureOpen();
         return delegate.read(b);
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException
      {
         ensureOpen();
         return delegate.read(b, off, len);
      }

      @Override
      public void close() throws IOException
      {
         if (closed)
         {
            return;
         }
         try
         {
            delegate.close();
         }
         catch (Exception e)
         {
            // shhh
         }
         try
         {
            zip.close();
         }
         catch (Exception e)
         {
            // shhh
         }
         closed = true;
         delegate = null;
         zip = null;
      }
   }
}
