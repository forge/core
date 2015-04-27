package org.jboss.forge.addon.resource.zip;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;
import org.jboss.forge.addon.resource.zip.ZipEntryResource;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class ZipEntryResourceImpl extends VirtualResource<FileHeader> implements ZipEntryResource
{
   private FileHeader fh;
   private ZipFile zipFile;
   public ZipEntryResourceImpl(ResourceFactory resourceFactory, ZipResourceImpl file,FileHeader fh)
   {
      super(resourceFactory,file);
      this.zipFile=file.getZipFile();
      this.fh=fh;
   }
   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not implemented");
   }
   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not implemented");
   }
   @Override
   public String getName()
   {
      return fh.getFileName();
   }
   @Override
   public FileHeader getUnderlyingResourceObject()
   {
     return fh;
   }
   @Override
   public void extract(DirectoryResource extractToDirectory)
   {
      try
      {
         zipFile.extractFile(fh, extractToDirectory.getFullyQualifiedName());
      }
      catch (ZipException e)
      {
         throw new ResourceException("Exception thrown when parsing zip entry file: "+ getFullyQualifiedName(),e);
      }
   }
   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

}
