package org.jboss.forge.resource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.facets.Facet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class URLResource extends VirtualResource<URL>
{
   private final URL resource;

   public URLResource(ResourceFactory factory, URL resource)
   {
      super(factory, null);
      this.resource = resource;
   }

   protected URLResource(ResourceFactory factory, Resource<?> parent, URL resource)
   {
      super(factory, parent);
      this.resource = resource;
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Cannot delete URL resources.");
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Cannot delete URL resources.");
   }

   @Override
   public String getName()
   {
      return resource.toExternalForm();
   }

   @Override
   public URL getUnderlyingResourceObject()
   {
      return resource;
   }

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return false;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return new ArrayList<Resource<?>>();
   }

}
