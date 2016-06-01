/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.builder;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Builder to create {@link Dependency} objects. This class implements {@link Dependency} for easy consumption. (I.e.:
 * Use this class wherever you need to create and use a new {@link Dependency})
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyBuilder implements Dependency
{
   private CoordinateBuilder coordinate;
   private String scopeType;
   private FileResource<?> artifact;
   private boolean optional;
   private List<Coordinate> exclusions;

   private DependencyBuilder()
   {
      this.coordinate = CoordinateBuilder.create();
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
      builder.setCoordinate(dep.getCoordinate());
      builder.setScopeType(dep.getScopeType());
      builder.setArtifact(dep.getArtifact());
      builder.setExcludedCoordinates(dep.getExcludedCoordinates());
      builder.setOptional(dep.isOptional());
      return builder;
   }

   /**
    * @param identifier of the form "groupId:artifactId", "groupId:artifactId:version", "groupId:artifactId:scope, "
    *           groupId :artifactId:version:scope", "groupId:artifactId:version:scope:packaging"
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
            dependencyBuilder.setPackaging(trimmed);
         }
      }

      return dependencyBuilder;
   }

   public DependencyBuilder addExclusion(Coordinate coordinate)
   {
      if (exclusions == null)
         exclusions = new ArrayList<>();

      exclusions.add(coordinate);
      return this;
   }

   @Override
   public List<Coordinate> getExcludedCoordinates()
   {
      return exclusions;
   }

   public DependencyBuilder setExcludedCoordinates(List<Coordinate> coordinates)
   {
      this.exclusions = coordinates;
      return this;
   }

   public DependencyBuilder setPackaging(String packaging)
   {
      getCoordinate().setPackaging(packaging);
      return this;
   }

   public DependencyBuilder setArtifactId(String artifactId)
   {
      getCoordinate().setArtifactId(artifactId);
      return this;
   }

   public DependencyBuilder setVersion(String version)
   {
      getCoordinate().setVersion(version);
      return this;
   }

   public DependencyBuilder setGroupId(String groupId)
   {
      getCoordinate().setGroupId(groupId);
      return this;
   }

   public DependencyBuilder setScopeType(final String scope)
   {
      this.scopeType = scope;
      return this;
   }

   public String getGroupId()
   {
      return getCoordinate().getGroupId();
   }

   @Override
   public String getScopeType()
   {
      return scopeType;
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
   public FileResource<?> getArtifact()
   {
      return artifact;
   }

   @Override
   public CoordinateBuilder getCoordinate()
   {
      return coordinate;
   }

   public DependencyBuilder setCoordinate(Coordinate coordinate)
   {
      this.coordinate = CoordinateBuilder.create(coordinate);
      return this;
   }

   public DependencyBuilder setArtifact(FileResource<?> artifact)
   {
      this.artifact = artifact;
      return this;
   }

   public DependencyBuilder setClassifier(String classifier)
   {
      getCoordinate().setClassifier(classifier);
      return this;
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      if (coordinate != null)
         builder.append(coordinate);
      return builder.toString();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
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
      DependencyBuilder other = (DependencyBuilder) obj;
      if (coordinate == null)
      {
         if (other.coordinate != null)
            return false;
      }
      else if (!coordinate.equals(other.coordinate))
         return false;
      return true;
   }

}
