/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.List;

import org.jboss.forge.project.packaging.PackagingType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ExcludedDependencyBuilder implements Dependency
{
   private final DependencyBuilder parent;
   private final DependencyImpl dep = new DependencyImpl();

   protected ExcludedDependencyBuilder(final DependencyBuilder parent)
   {
      this.parent = parent;
   }

   public static ExcludedDependencyBuilder create(final DependencyBuilder parent)
   {
      return new ExcludedDependencyBuilder(parent);
   }

   public ExcludedDependencyBuilder setGroupId(final String groupId)
   {
      dep.setGroupId(groupId);
      return this;
   }

   public ExcludedDependencyBuilder setArtifactId(final String artifactId)
   {
      dep.setArtifactId(artifactId);
      return this;
   }

   @Override
   public String getArtifactId()
   {
      return dep.getArtifactId();
   }

   @Override
   public String getGroupId()
   {
      return dep.getGroupId();
   }

   /**
    * Not implemented for Exclusions. Always returns <code>""</code>.
    */
   @Override
   public String getVersion()
   {
      return "";
   }

   /**
    * Not implemented for Exclusions. Always returns <code>null</code>.
    */
   @Override
   public String getScopeType()
   {
      throw new IllegalStateException("Not implemented for Exclusions");
   }

   @Override
   public PackagingType getPackagingTypeEnum()
   {
      throw new IllegalStateException("Not implemented for Exclusions");
   }

   @Override
   public ScopeType getScopeTypeEnum()
   {
      throw new IllegalStateException("Not implemented for Exclusions");
   }

   @Override
   public List<Dependency> getExcludedDependencies()
   {
      return dep.getExcludedDependencies();
   }

   /*
    * DependencyBuilder scoped methods.
    */

   public ExcludedDependencyBuilder addExclusion()
   {
      return parent.addExclusion(parent);
   }

   public DependencyBuilder setVersion(final String version)
   {
      parent.setVersion(version);
      return parent;
   }

   public DependencyBuilder setScope(final ScopeType scope)
   {
      parent.setScopeType(scope);
      return parent;
   }

   @Override
   public String getPackagingType()
   {
      throw new IllegalStateException("Not implemented for Exclusions");
   }

   @Override
   public String getClassifier()
   {
      throw new IllegalStateException("Not implemented for Exclusions");
   }

   @Override
   public boolean isSnapshot()
   {
      throw new IllegalStateException("Not implemented for Exclusions");
   }

   @Override
   public String toCoordinates()
   {
      return DependencyBuilder.toId(this);
   }

   @Override
   public String toString()
   {
      return DependencyBuilder.toString(this);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dep == null) ? 0 : dep.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      ExcludedDependencyBuilder other = (ExcludedDependencyBuilder) obj;
      if (dep == null)
      {
         if (other.dep != null)
         {
            return false;
         }
      }
      else if (!dep.equals(other.dep))
      {
         return false;
      }
      return true;
   }
}
