/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency;


/**
 * Builder to create {@link DependencyImpl} objects. This class implements {@link DependencyImpl} for easy consumption. (I.e.:
 * Use this class wherever you need to create and use a new {@link DependencyImpl})
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyBuilder implements Dependency
{
   private final DependencyImpl dep = new DependencyImpl();

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
   public static boolean areEquivalent(final DependencyImpl l, final DependencyImpl r)
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
      dep.setGroupId(groupId);
      return this;
   }

   public DependencyBuilder setArtifactId(final String artifactId)
   {
      dep.setArtifactId(artifactId);
      return this;
   }

   public DependencyBuilder setVersion(final String version)
   {
      dep.setVersion(version);
      return this;
   }

   public DependencyBuilder setScopeType(final String scope)
   {
      dep.setScopeType(scope);
      return this;
   }

   public DependencyBuilder setPackagingType(final String type)
   {
      dep.setPackagingType(type);
      return this;
   }

   public DependencyBuilder setClassifier(final String classifier)
   {
      dep.setClassifier(classifier);
      return this;
   }

   public DependencyBuilder setSystemPath(final String systemPath)
   {
      dep.setSystemPath(systemPath);
      return this;
   }

   @Override
   public String getSystemPath()
   {
      return dep.getSystemPath();
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

   @Override
   public String getVersion()
   {
      return dep.getVersion();
   }

   @Override
   public String getScopeType()
   {
      return dep.getScopeType();
   }

   @Override
   public boolean isSnapshot()
   {
      return dep.isSnapshot();
   }

   @Override
   public String getPackagingType()
   {
      return dep.getPackagingType();
   }

   @Override
   public String getClassifier()
   {
      return dep.getClassifier();
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

   /**
    * Convenience method which should be used to convert a {@link DependencyImpl} object into its string representation, for
    * example: "groupId:artifactId:version:scope:packaging"
    */
   public static String toString(final Dependency dep)
   {
      String gav = dep.toCoordinates();
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
      return DependencyBuilder.toString(dep);
   }

   @Override
   public boolean isOptional()
   {
      return dep.isOptional();
   }

   public DependencyBuilder setOptional(boolean optional)
   {
      dep.setOptional(optional);
      return this;
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
      DependencyBuilder other = (DependencyBuilder) obj;
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
