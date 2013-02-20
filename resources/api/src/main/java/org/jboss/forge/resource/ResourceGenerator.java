package org.jboss.forge.resource;


public interface ResourceGenerator<RESOURCETYPE, UNDERLYINGTYPE>
{
   public boolean handles(Class<?> type, final Object resource);

   public <T extends Resource<UNDERLYINGTYPE>> T getResource(final ResourceFactory factory, Class<RESOURCETYPE> type,
            final UNDERLYINGTYPE resource);

   public <T extends Resource<UNDERLYINGTYPE>> Class<?> getResourceType(Class<RESOURCETYPE> type,
            final UNDERLYINGTYPE resource);

}