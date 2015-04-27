package org.jboss.forge.addon.resource.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.zip.ZipResource;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;


public class ZipResourceImpl extends AbstractFileResource<ZipResource> implements ZipResource
{

   private ZipFile zipFile;
   

   public ZipResourceImpl(ResourceFactory factory, File file)
   {
      super(factory, file);
      try
      {
         zipFile = new ZipFile(getFullyQualifiedName());
      }
      catch (ZipException e)
      {
         throw new IllegalArgumentException("Exception thrown when accessing zip file: "+ getFullyQualifiedName(),e);
      }
   }
   
   public ZipFile getZipFile()
   {
      return zipFile;
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      if (getFileOperations().fileExists(file) && getFileOperations().fileExistsAndIsDirectory(file))
      {
         throw new ResourceException("File reference is not a zip file: " + file.getAbsolutePath());
      }
      else if (!getFileOperations().fileExists(file))
      {
         throw new ResourceException("File reference does not exist: " + file.getAbsolutePath());
      }
      return new ZipResourceImpl(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      try
      {
         List fileHeaders = zipFile.getFileHeaders();
         List<Resource<?>> result = new ArrayList<Resource<?>>();
         for(Object fh : fileHeaders){
            if(fh instanceof FileHeader) {
               FileHeader header = (FileHeader) fh;
               ZipEntryResourceImpl resource = new ZipEntryResourceImpl(getResourceFactory(),this,header);
               result.add(resource);
            }
         }
         return result;
      }
      catch (ZipException e)
      {
         throw new ResourceException("Exception thrown when parsing zip file: "+ getFullyQualifiedName(),e);
      }
   }

   @Override
   public void unzipAll(DirectoryResource unzipToDir)
   {
      try
      {
         zipFile.extractAll(unzipToDir.getFullyQualifiedName());
      }
      catch (ZipException e)
      {
         throw new ResourceException("Exception thrown when parsing zip file: "+ getFullyQualifiedName(),e);
      }
      
   }

}
