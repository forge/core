/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import org.jboss.forge.project.services.ResourceFactory;

/**
 * A resource for representing zip archive files (zip, jar, war, ear).
 * 
 * @author Adolfo Junior
 */
@ResourceHandles({ "*.zip", "*.jar", "*.war", "*.ear" })
public class ZipResource extends FileResource<ZipResource>
{
   private static final String SLASH = "/";

   private ZipEntryResource root;

   @Inject
   public ZipResource(final ResourceFactory factory)
   {
      super(factory, null);
      setFlag(ResourceFlag.Leaf);
   }

   public ZipResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public ZipResource createFrom(File file)
   {
      return new ZipResource(resourceFactory, file);
   }

   /**
    * Delegate to root {@link ZipEntryResource#listResources(ResourceFilter, boolean)}
    */
   public synchronized List<Resource<?>> listResources(final ResourceFilter filter, boolean recursively)
   {
      return getRootEntry().listResources(filter, recursively);
   }

   /**
    * Delegate to root {@link ZipEntryResource#doListResources()}
    */
   @Override
   protected List<Resource<?>> doListResources()
   {
      return getRootEntry().doListResources();
   }

   /**
    * Initialize entries in first access or if the zip is stale.
    */
   public ZipEntryResource getRootEntry()
   {
      if (root == null || isStale())
      {
         readEntries(root = new ZipEntryResource(resourceFactory, this));
      }
      return root;
   }

   /**
    * Read all the zip entries and create the hierarchical tree.
    */
   protected void readEntries(ZipEntryResource parent)
   {
      ZipFile zip = null;
      try
      {
         zip = new ZipFile(file);
         Enumeration<? extends ZipEntry> entries = zip.entries();
         while (entries.hasMoreElements())
         {
            addEntry(parent, entries.nextElement());
         }
      }
      catch (IOException io)
      {
         throw new ResourceException("Error reading entries of zip file [" + getFullyQualifiedName() + "]", io);
      }
      finally
      {
         closeQuietily(zip);
      }
   }

   /**
    * Split the entry path by {@value #SLASH} and add recursively.
    * 
    * @see ZipEntryResource#getChildOrCreate(String)
    */
   protected void addEntry(ZipEntryResource parent, ZipEntry entry)
   {
      String[] names = trimSlash(entry.getName()).split(SLASH);

      ZipEntryResource last = parent;

      for (int i = 0; i < names.length; i++)
      {
         last = last.getChildOrCreate(names[i]);
      }

      last.setFlag(entry.isDirectory() ? ResourceFlag.Node : ResourceFlag.Leaf);
   }

   /**
    * Obtain a reference to the child resource.
    */
   @Override
   public ZipEntryResource getChild(final String name)
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

   protected File getFile()
   {
      return file;
   }

   private String trimSlash(String name)
   {
      if (name.startsWith(SLASH))
      {
         name = name.substring(1);
      }
      if (name.endsWith(SLASH))
      {
         name = name.substring(0, name.length() - 1);
      }
      return name;
   }

   @Override
   public String toString()
   {
      return file.getName();
   }

   /*
    * ZipFile is closable only in Java7
    */
   private void closeQuietily(ZipFile zip)
   {
      try
      {
         if (zip != null)
         {
            zip.close();
         }
      }
      catch (IOException e)
      {
         // shhhh
      }
   }
}
