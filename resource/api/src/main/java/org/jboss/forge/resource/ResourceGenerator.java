package org.jboss.forge.resource;

public interface ResourceGenerator<UNDERLYINGTYPE>
{
   public Class<? extends Resource<UNDERLYINGTYPE>> getResourceType(UNDERLYINGTYPE resource);

   public <T extends Resource<UNDERLYINGTYPE>> T getResource(final ResourceFactory factory,
            final UNDERLYINGTYPE resource);

   public boolean handles(final Object resource);
}