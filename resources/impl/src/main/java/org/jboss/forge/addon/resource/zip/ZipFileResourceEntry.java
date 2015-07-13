/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.zip;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;
import org.jboss.forge.furnace.util.Assert;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

/**
 * Entries for a {@link ZipFileResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ZipFileResourceEntry extends VirtualResource<ZipFileResource>
{
   private final FileHeader fileHeader;

   public ZipFileResourceEntry(ResourceFactory factory, ZipFileResource parent, FileHeader fileHeader)
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
   public ZipFileResource getUnderlyingResourceObject()
   {
      return (ZipFileResource) getParent();
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

   private ZipFile getZipFile()
   {
      ZipFileResourceImpl impl = (ZipFileResourceImpl) getParent();
      return impl.getZipFile();
   }

}
