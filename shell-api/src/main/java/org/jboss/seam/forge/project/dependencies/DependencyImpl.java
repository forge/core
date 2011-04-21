/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.forge.project.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.forge.project.packaging.PackagingType;

/**
 * This class is internal; instead use {@link DependencyBuilder} for {@link Dependency} creation & instantiation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyImpl implements Dependency
{
   private String groupId;
   private String artifactId;
   private String version;
   private String scopeType;
   private String packagingType;
   private String classifier;
   private List<Dependency> excludedDependencies = new ArrayList<Dependency>();

   DependencyImpl()
   {
   }

   @Override
   public String getArtifactId()
   {
      return artifactId;
   }

   @Override
   public String getGroupId()
   {
      return groupId;
   }

   @Override
   public String getVersion()
   {
      return version;
   }

   @Override
   public String getScopeType()
   {
      return scopeType;
   }

   @Override
   public PackagingType getPackagingTypeEnum()
   {
      return PackagingType.from(getPackagingType());
   }

   @Override
   public ScopeType getScopeTypeEnum()
   {
      return ScopeType.from(getScopeType());
   }

   public void setScopeType(final ScopeType scope)
   {
      this.scopeType = scope == null ? null : scope.getScope();
   }

   public void setScopeType(final String type)
   {
      scopeType = type;
   }

   public void setVersion(final String version)
   {
      this.version = version;
   }

   public void setGroupId(final String groupId)
   {
      this.groupId = groupId;
   }

   public void setArtifactId(final String artifactId)
   {
      this.artifactId = artifactId;
   }

   @Override
   public String getClassifier()
   {
      return classifier;
   }

   public void setClassifier(String classifier)
   {
      this.classifier = classifier;
   }

   @Override
   public List<Dependency> getExcludedDependencies()
   {
      return excludedDependencies;
   }

   public void setExcludedDependencies(final List<Dependency> excludedDependencies)
   {
      this.excludedDependencies = excludedDependencies;
   }

   @Override
   public String getPackagingType()
   {
      return packagingType;
   }

   public void setPackagingType(final PackagingType packagingType)
   {
      this.packagingType = packagingType == null ? null : packagingType.getType();
   }

   public void setPackagingType(final String type)
   {
      packagingType = type;
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
      result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
      result = prime * result + ((excludedDependencies == null) ? 0 : excludedDependencies.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((packagingType == null) ? 0 : packagingType.hashCode());
      result = prime * result + ((scopeType == null) ? 0 : scopeType.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof Dependency))
      {
         return false;
      }

      Dependency that = (Dependency) o;

      boolean exclusionsEqual = false;

      if (excludedDependencies != null)
      {
         if (that.getExcludedDependencies() != null)
         {
            List<Dependency> temp = new ArrayList<Dependency>();
            temp.addAll(excludedDependencies);
            if (temp.containsAll(that.getExcludedDependencies()))
            {
               temp.removeAll(that.getExcludedDependencies());
               if (temp.isEmpty())
               {
                  exclusionsEqual = true;
               }
            }
         }
      }
      else
      {
         exclusionsEqual = that.getExcludedDependencies() == null;
      }

      return !(artifactId != null ? !artifactId.equals(that.getArtifactId()) : that.getArtifactId() != null)
               && exclusionsEqual
               && !(groupId != null ? !groupId.equals(that.getGroupId()) : that.getGroupId() != null)
               && !(packagingType != null ? !packagingType.equals(that.getPackagingType())
                        : that.getPackagingType() != null) &&
               !(scopeType != null ? !scopeType.equals(that.getScopeType()) : that.getScopeType() != null) &&
               !(version != null ? !version.equals(that.getVersion()) : that.getVersion() != null) &&
               !(classifier != null ? !classifier.equals(that.getClassifier()) : that.getClassifier() != null);

   }

}
