package org.jboss.forge.resource;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.facets.Facet;

public class UnknownFileResource extends FileResource<UnknownFileResource>
{
   protected UnknownFileResource(ResourceFactory factory, File file)
   {
      super(factory, file);
   }

   @Override
   public UnknownFileResource createFrom(File file)
   {
      return new UnknownFileResource(resourceFactory, file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public String toString()
   {
      return file.getName();
   }

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return false;
   }
}
