package org.jboss.forge.resources;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
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
   private List<Resource<?>> listCache;

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

   @Override
   public String toString()
   {
      return file.getName();
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      if (isStale())
      {
         listCache = null;
      }

      if (listCache == null)
      {
         listCache = new LinkedList<Resource<?>>();

         ZipFile zip = null;
         try
         {
            zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements())
            {
               listCache.add(getChild(entries.nextElement().getName()));
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
      return listCache.isEmpty() ? Collections.<Resource<?>> emptyList() : listCache;
   }

   protected InputStream getEntryInputStream(String entryName)
   {
      ZipFile zip = null;
      try
      {
         zip = new ZipFile(file);
         return new BufferedInputStream(zip.getInputStream(zip.getEntry(entryName)));
      }
      catch (IOException io)
      {
         throw new ResourceException("Error reading content of [" + entryName + "] in zip file ["
                  + getFullyQualifiedName() + "]", io);
      }
      finally
      {
         closeQuietily(zip);
      }
   }

   /**
    * Obtain a reference to the child resource.
    */
   @Override
   public Resource<?> getChild(final String name)
   {
      return new ZipEntryResource(resourceFactory, this, name);
   }

   /*
    * ZipFile is closable only in Java7
    */
   private void closeQuietily(ZipFile zip)
   {
      try
      {
         zip.close();
      }
      catch (IOException e)
      {
         // shhhh
      }
   }
}
