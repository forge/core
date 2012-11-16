package org.jboss.forge.resources;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.project.services.ResourceFactory;

/**
 * Represents an entry in zip archive.
 * 
 * @author Adolfo Junior
 */
public class ZipEntryResource extends AbstractResource<String>
{
   private String name;

   public ZipEntryResource(ResourceFactory factory, ZipResource parent, String entry)
   {
      super(factory, parent);
      this.name = entry;
   }

   @Override
   public ZipResource getParent()
   {
      return (ZipResource) super.getParent();
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      return false;
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      return false;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public ZipEntryResource createFrom(String entry)
   {
      return new ZipEntryResource(getResourceFactory(), getParent(), entry);
   }

   @Override
   public String getUnderlyingResourceObject()
   {
      return name;
   }

   @Override
   public InputStream getResourceInputStream()
   {
      return getParent().getEntryInputStream(getName());
   }

   @Override
   public Resource<?> getChild(String name)
   {
      return null;
   }

   @Override
   public boolean exists()
   {
      return true;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }
}