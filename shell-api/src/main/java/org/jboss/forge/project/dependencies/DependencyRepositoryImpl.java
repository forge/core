/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DependencyRepositoryImpl implements DependencyRepository
{
   private final String id;
   private final String url;

   public DependencyRepositoryImpl(final String id, final String url)
   {
      if (Strings.isNullOrEmpty(id))
      {
         throw new IllegalArgumentException("must specify repository id");
      }
      if (Strings.isNullOrEmpty(url))
      {
         throw new IllegalArgumentException("must specify repository url");
      }
      this.id = id;
      this.url = url;
   }

   public DependencyRepositoryImpl(KnownRepository repo)
   {
      this(repo.getId(), repo.getUrl());
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public String getUrl()
   {
      return url;
   }

   @Override
   public String toString()
   {
      return "[id=" + id + ", url=" + url + "]";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DependencyRepositoryImpl other = (DependencyRepositoryImpl) obj;
      if (id == null)
      {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      if (url == null)
      {
         if (other.url != null)
            return false;
      }
      else if (!url.equals(other.url))
         return false;
      return true;
   }

}
