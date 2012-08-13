/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.VirtualResource;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @version $Revision: $
 */
public class MavenRepositoryResource extends VirtualResource<Repository>
{
   private final Repository repo;

   public MavenRepositoryResource(Resource<?> parent, Repository repository)
   {
      super(parent);
      this.repo = repository;
   }

   @Override
   public String getName()
   {
      return repo.getId();
   }

   public String getURL()
   {
      return repo.getUrl();
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> children = new ArrayList<Resource<?>>();
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
}
