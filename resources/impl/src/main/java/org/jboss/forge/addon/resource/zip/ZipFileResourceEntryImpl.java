/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.zip;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;
import org.jboss.forge.furnace.util.Assert;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;

/**
 * Entries for a {@link ZipFileResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ZipFileResourceEntryImpl extends VirtualResource<String>implements ZipFileResourceEntry
{
   private final FileHeader fileHeader;

   public ZipFileResourceEntryImpl(ResourceFactory factory, ZipFileResource parent, FileHeader fileHeader)
   {
      super(factory, parent);
      Assert.notNull(fileHeader, "File header should not be null");
      this.fileHeader = fileHeader;
   }

   @Override
   public boolean delete()
   {
      try
      {
         getZipFile().removeFile(fileHeader);
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while deleting zip entry", e);
      }
      return true;
   }

   @Override
   public InputStream getResourceInputStream()
   {
      try
      {
         return getZipFile().getInputStream(fileHeader);
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while fetching zip contents", e);
      }
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      return delete();
   }

   @Override
   public String getName()
   {
      return fileHeader.getFileName();
   }

   @Override
   public String getUnderlyingResourceObject()
   {
      return getName();
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public String toString()
   {
      return getName();
   }

   @Override
   public void extractTo(DirectoryResource directoryResource, String newName)
   {
      try
      {
         UnzipParameters parameters = new UnzipParameters();
         getZipFile().extractFile(fileHeader, directoryResource.getFullyQualifiedName(), parameters, newName);
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while fetching zip contents", e);
      }
   }

   @Override
   public void extractTo(DirectoryResource directoryResource)
   {
      try
      {
         UnzipParameters parameters = new UnzipParameters();
         getZipFile().extractFile(fileHeader, directoryResource.getFullyQualifiedName(), parameters);
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while fetching zip contents", e);
      }
   }

   @Override
   public boolean isDirectory()
   {
      return fileHeader.isDirectory();
   }

   private ZipFile getZipFile()
   {
      ZipFileResourceImpl impl = (ZipFileResourceImpl) getParent();
      return impl.getZipFile();
   }

}
