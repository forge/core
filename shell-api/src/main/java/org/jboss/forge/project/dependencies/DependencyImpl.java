/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.project.packaging.PackagingType;

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
   private String systemPath;

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

   public String getSystemPath()
   {
      return systemPath;
   }

   public void setSystemPath(String systemPath)
   {
      this.systemPath = systemPath;
   }

   @Override
   public String toCoordinates()
   {
      return DependencyBuilder.toId(this);
   }

   @Override
   public boolean isSnapshot()
   {
      return getVersion().endsWith("-SNAPSHOT");
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
      result = prime * result + ((systemPath== null) ? 0 : systemPath.hashCode());
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

      boolean artifactIdEquals = artifactId != null ? artifactId.equals(that.getArtifactId())
               : that.getArtifactId() == null;
      boolean groupIdEquals = groupId != null ? groupId.equals(that.getGroupId()) : that.getGroupId() == null;
      boolean packagingTypeEquals = packagingType != null ? packagingType.equals(that.getPackagingType()) : that
               .getPackagingType() == null;
      boolean scopeTypeEquals = scopeType != null ? scopeType.equals(that.getScopeType()) : that.getScopeType() == null;
      boolean versionEquals = version != null ? version.equals(that.getVersion()) : that.getVersion() == null;
      boolean classifierEquals = classifier != null ? classifier.equals(that.getClassifier())
               : that.getClassifier() == null;
      boolean systemPathEquals = systemPath != null ? systemPath.equals(that.getSystemPath())
               : that.getSystemPath() == null;

      return artifactIdEquals && exclusionsEqual && groupIdEquals && packagingTypeEquals && scopeTypeEquals
               && versionEquals && classifierEquals && systemPathEquals;

   }

}
