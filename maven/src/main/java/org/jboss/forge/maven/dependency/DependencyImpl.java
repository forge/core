/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is internal; instead use {@link DependencyBuilder} for {@link DependencyImpl} creation & instantiation.
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
   private File artifact;
   private boolean optional;

   private Set<Dependency> excludedDependencies = new HashSet<Dependency>();

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
   public String getPackagingType()
   {
      return packagingType;
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
   public boolean isOptional()
   {
      return optional;
   }

   public void setOptional(boolean optional)
   {
      this.optional = optional;
   }

   public void setArtifact(File artifact)
   {
      this.artifact = artifact;
   }

   public File getArtifact()
   {
      return artifact;
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
      result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
      result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
      result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
      result = prime * result + ((excludedDependencies == null) ? 0 : excludedDependencies.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + (optional ? 1231 : 1237);
      result = prime * result + ((packagingType == null) ? 0 : packagingType.hashCode());
      result = prime * result + ((scopeType == null) ? 0 : scopeType.hashCode());
      result = prime * result + ((systemPath == null) ? 0 : systemPath.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      DependencyImpl other = (DependencyImpl) obj;
      if (artifact == null)
      {
         if (other.artifact != null)
            return false;
      }
      else if (!artifact.equals(other.artifact))
         return false;
      if (artifactId == null)
      {
         if (other.artifactId != null)
            return false;
      }
      else if (!artifactId.equals(other.artifactId))
         return false;
      if (classifier == null)
      {
         if (other.classifier != null)
            return false;
      }
      else if (!classifier.equals(other.classifier))
         return false;
      if (excludedDependencies == null)
      {
         if (other.excludedDependencies != null)
            return false;
      }
      else if (!excludedDependencies.equals(other.excludedDependencies))
         return false;
      if (groupId == null)
      {
         if (other.groupId != null)
            return false;
      }
      else if (!groupId.equals(other.groupId))
         return false;
      if (optional != other.optional)
         return false;
      if (packagingType == null)
      {
         if (other.packagingType != null)
            return false;
      }
      else if (!packagingType.equals(other.packagingType))
         return false;
      if (scopeType == null)
      {
         if (other.scopeType != null)
            return false;
      }
      else if (!scopeType.equals(other.scopeType))
         return false;
      if (systemPath == null)
      {
         if (other.systemPath != null)
            return false;
      }
      else if (!systemPath.equals(other.systemPath))
         return false;
      if (version == null)
      {
         if (other.version != null)
            return false;
      }
      else if (!version.equals(other.version))
         return false;
      return true;
   }

}
