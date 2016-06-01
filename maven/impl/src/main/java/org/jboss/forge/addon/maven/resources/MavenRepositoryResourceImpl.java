/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFacet;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenRepositoryResourceImpl extends VirtualResource<Repository>implements MavenRepositoryResource
{
   private final Repository repo;

   public MavenRepositoryResourceImpl(ResourceFactory factory, Resource<?> parent, Repository repository)
   {
      super(factory, parent);
      this.repo = repository;
   }

   @Override
   public String getName()
   {
      return repo.getId();
   }

   @Override
   public String getURL()
   {
      return repo.getUrl();
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> children = new ArrayList<>();
      return children;
   }

   @Override
   public Repository getUnderlyingResourceObject()
   {
      return repo;
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("not supported");
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("not supported");
   }

   @Override
   public <F extends ResourceFacet> boolean supports(F facet)
   {
      return false;
   }
}
