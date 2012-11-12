/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.io.File;

/**
 * Builder to create {@link Dependency} objects. This class implements {@link Dependency} for easy consumption. (I.e.:
 * Use this class wherever you need to create and use a new {@link Dependency})
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyBuilder implements Dependency
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

   private DependencyBuilder()
   {
   }

   /**
    * Obtain a new {@link DependencyBuilder} instance.
    */
   public static DependencyBuilder create()
   {
      return new DependencyBuilder();
   }

   public static DependencyBuilder create(final Dependency dep)
   {
      DependencyBuilder builder = new DependencyBuilder();
      builder.setGroupId(dep.getGroupId());
      builder.setArtifactId(dep.getArtifactId());
      builder.setVersion(dep.getVersion());
      builder.setPackagingType(dep.getPackagingType());
      builder.setScopeType(dep.getScopeType());
      builder.setClassifier(dep.getClassifier());
      builder.setSystemPath(dep.getSystemPath());
      return builder;
   }

   /**
    * Return true if the groupId and artifactId of the two given dependencies are equal.
    */
   public static boolean areEquivalent(final Dependency l, final Dependency r)
   {
      if (l == r)
      {
         return true;
      }
      if ((l == null) && (r == null))
      {
         return true;
      }
      else if ((l == null) || (r == null))
      {
         return false;
      }

      return !(l.getArtifactId() != null ? !l.getArtifactId().equals(r.getArtifactId()) : r.getArtifactId() != null) &&
               !(l.getGroupId() != null ? !l.getGroupId().equals(r.getGroupId()) : r.getGroupId() != null) &&
               !(l.getClassifier() != null ? !l.getClassifier().equals(r.getClassifier()) : r.getClassifier() != null);

   }

   /**
    * @param identifier of the form "groupId:artifactId", "groupId:artifactId:version",
    *           "groupId:artifactId:scope, "groupId
    *           :artifactId:version:scope", "groupId:artifactId:version:scope:packaging"
    *
    *           For classifier specification, see {@link #setClassifier(String)}
    */
   public static DependencyBuilder create(final String identifier)
   {
      DependencyBuilder dependencyBuilder = new DependencyBuilder();

      if (identifier != null)
      {
         String[] split = identifier.split(":");
         if (split.length > 0)
         {
            dependencyBuilder.setGroupId(split[0].trim());
         }
         if (split.length > 1)
         {
            dependencyBuilder.setArtifactId(split[1].trim());
         }
         if (split.length > 2)
         {
            dependencyBuilder.setVersion(split[2].trim());
         }
         if (split.length > 3)
         {
            String trimmed = split[3].trim();
            dependencyBuilder.setScopeType(trimmed);
         }
         if (split.length > 4)
         {
            String trimmed = split[4].trim();
            dependencyBuilder.setPackagingType(trimmed);
         }
      }

      return dependencyBuilder;
   }

   public DependencyBuilder setGroupId(final String groupId)
   {
      this.groupId = groupId;
      return this;
   }

   public DependencyBuilder setArtifactId(final String artifactId)
   {
      this.artifactId = artifactId;
      return this;
   }

   public DependencyBuilder setVersion(final String version)
   {
      this.version = version;
      return this;
   }

   public DependencyBuilder setScopeType(final String scope)
   {
      this.scopeType = scope;
      return this;
   }

   public DependencyBuilder setPackagingType(final String type)
   {
      this.packagingType = type;
      return this;
   }

   public DependencyBuilder setClassifier(final String classifier)
   {
      this.classifier = classifier;
      return this;
   }

   public DependencyBuilder setSystemPath(final String systemPath)
   {
      this.systemPath = systemPath;
      return this;
   }

   @Override
   public String getSystemPath()
   {
      return systemPath;
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
   public boolean isSnapshot()
   {
      return getVersion() != null && getVersion().endsWith("SNAPSHOT");
   }

   @Override
   public String getPackagingType()
   {
      return packagingType;
   }

   @Override
   public String getClassifier()
   {
      return classifier;
   }

   /**
    * Convenience method which should be used to convert a {@link DependencyImpl} object into its id representation, for
    * example: "groupId:artifactId:::version", "groupId:artifactId:packaging::version" or
    * "groupId:artifactId:packaging:classifier:version"
    *
    * @see {@link DependencyImpl#toCoordinates()}
    */
   public static String toId(final Dependency dep)
   {
      String gav = (dep.getGroupId() + ":" + dep.getArtifactId());
      gav += ":" + (dep.getPackagingType() == null ? "" : dep.getPackagingType());
      gav += ":" + (dep.getClassifier() == null ? "" : dep.getClassifier());
      gav += ":" + (dep.getVersion() == null ? "" : dep.getVersion());
      return gav;
   }

   @Override
   public String toCoordinates()
   {
      return toId(this);
   }

   @Override
   public String toString()
   {
      return toCoordinates();
   }

   @Override
   public boolean isOptional()
   {
      return this.optional;
   }

   public DependencyBuilder setOptional(boolean optional)
   {
      this.optional = optional;
      return this;
   }

   @Override
   public File getArtifact()
   {
      return artifact;
   }

   public DependencyBuilder setArtifact(File artifact)
   {
      this.artifact = artifact;
      return this;
   }

}
