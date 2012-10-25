/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class DependencyRepository
{
   public static final DependencyRepository CENTRAL = new DependencyRepository("central",
            "http://repo1.maven.org/maven2/");
   public static final DependencyRepository JBOSS_NEXUS = new DependencyRepository("jboss",
            "http://repository.jboss.org/nexus/content/groups/public");
   public static final DependencyRepository JAVA_NET = new DependencyRepository("java_net",
            "http://download.java.net/maven/2/");

   private final String id;
   private final String url;

   public DependencyRepository(final String id, final String url)
   {
      if (id == null || id.isEmpty())
      {
         throw new IllegalArgumentException("must specify repository id");
      }
      if (url == null || url.isEmpty())
      {
         throw new IllegalArgumentException("must specify repository url");
      }
      this.id = id;
      this.url = url;
   }

   public String getId()
   {
      return id;
   }

   public String getUrl()
   {
      return url;
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
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DependencyRepository other = (DependencyRepository) obj;
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

   @Override
   public String toString()
   {
      return "DependencyRepository [id=" + id + ", url=" + url + "]";
   }
}
