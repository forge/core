/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Strings;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

/**
 * Implementation for the {@link ZipFileResource} interface
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ZipFileResourceImpl extends AbstractFileResource<ZipFileResource>implements ZipFileResource
{
   private final ZipFile zipFile;

   public ZipFileResourceImpl(ResourceFactory resourceFactory, File file)
   {
      super(resourceFactory, file);
      try
      {
         this.zipFile = new ZipFile(file);
      }
      catch (ZipException e)
      {
         // This is only thrown when file is null, it should never happen
         throw new ResourceException("Error while creating ZipFile", e);
      }
   }

   ZipFile getZipFile()
   {
      return zipFile;
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new ZipFileResourceImpl(getResourceFactory(), file);
   }

   @Override
   public Resource<?> getChild(String name)
   {
      FileHeader fileHeader;
      try
      {
         fileHeader = zipFile.getFileHeader(name);
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while fetching file header", e);
      }
      return (fileHeader == null) ? null : new ZipFileResourceEntryImpl(getResourceFactory(), this, fileHeader);
   }

   @Override
   @SuppressWarnings("unchecked")
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> entries = new ArrayList<>();
      try
      {
         List<FileHeader> fileHeaders = zipFile.getFileHeaders();
         for (FileHeader fileHeader : fileHeaders)
         {
            entries.add(new ZipFileResourceEntryImpl(getResourceFactory(), this, fileHeader));
         }
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while listing children", e);
      }
      return entries;
   }

   @Override
   public void extractTo(DirectoryResource directoryResource)
   {
      try
      {
         getZipFile().extractAll(directoryResource.getFullyQualifiedName());
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while unzipping files", e);
      }
   }

   @Override
   public boolean isEncrypted()
   {
      try
      {
         return getZipFile().isEncrypted();
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while checking if file is encrypted", e);
      }
   }

   @Override
   public ZipFileResource setPassword(char[] password)
   {
      try
      {
         getZipFile().setPassword(password);
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while setting the zip password", e);
      }
      return this;
   }

   @Override
   public ZipFileResource add(FileResource<?>... resources)
   {
      Assert.notNull(resources, "You cannot add null resources to a zip file");
      ArrayList<File> files = new ArrayList<>(resources.length);
      ArrayList<File> directories = new ArrayList<>(resources.length);

      for (FileResource<?> resource : resources)
      {
         if (resource.isDirectory())
         {
            directories.add(resource.getUnderlyingResourceObject());
         }
         else
         {
            files.add(resource.getUnderlyingResourceObject());
         }
      }
      try
      {
         ZipParameters parameters = new ZipParameters();
         for (File directory : directories)
         {
            getZipFile().addFolder(directory, parameters);
         }
         if (files.size() > 0)
         {
            getZipFile().addFiles(files, parameters);
         }
      }
      catch (ZipException e)
      {
         throw new ResourceException("Error while adding files to zip file", e);
      }
      return this;
   }

   @Override
   public ZipFileResource add(String name, Resource<?> resource)
   {
      Assert.isTrue(!Strings.isNullOrEmpty(name), "You need to specify a name for the Resource");
      Assert.notNull(resource, "You cannot add a null Resource to a zip file");
      try
      {
         ZipParameters parameters = new ZipParameters();
         parameters.setFileNameInZip(name);
         if (resource instanceof DirectoryResource)
         {
            for (Resource child : resource.listResources())
            {
               add(name + File.separatorChar + child.getName(), child);
            }
         }
         else
         {
            parameters.setSourceExternalStream(true);
            try (InputStream stream = resource.getResourceInputStream())
            {
               getZipFile().addStream(stream, parameters);
            }
         }
      }
      catch (IOException | ZipException e)
      {
         throw new ResourceException("Error while adding files to zip file", e);
      }
      return this;
   }
}
