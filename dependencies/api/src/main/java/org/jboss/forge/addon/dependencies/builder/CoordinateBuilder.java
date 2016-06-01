/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.builder;

import java.util.Map;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.furnace.util.Strings;

public class CoordinateBuilder implements Coordinate
{
   private String groupId;
   private String artifactId;
   private String version;
   private String classifier;
   private String packaging;
   private String systemPath;

   /**
    * 
    * Creates a Coordinate
    * 
    * @param coordinates The artifact coordinates in the format
    *           {@code <groupId>:<artifactId>[:<packaging>[:<classifier>]]:<version>} , must not be {@code null} or
    *           empty.
    * 
    * @return
    */
   public static CoordinateBuilder create(String coordinates)
   {
      CoordinateBuilder builder = CoordinateBuilder.create();
      // groupId:artifactId:packaging:classifier:version
      String[] split = coordinates.split("\\:");
      switch (split.length)
      {
      case 1:
         builder.setGroupId(split[0]);
         break;
      case 2:
         builder.setGroupId(split[0]);
         builder.setArtifactId(split[1]);
         break;
      case 3:
         builder.setGroupId(split[0]);
         builder.setArtifactId(split[1]);
         builder.setVersion(split[2]);
         break;
      case 4:
         builder.setGroupId(split[0]);
         builder.setArtifactId(split[1]);
         builder.setPackaging(split[2]);
         builder.setVersion(split[3]);
         break;
      case 5:
         builder.setGroupId(split[0]);
         builder.setArtifactId(split[1]);
         builder.setPackaging(split[2]);
         builder.setClassifier(split[3]);
         builder.setVersion(split[4]);
         break;
      default:
         throw new IllegalArgumentException(
                  "Malformed coordinate (" + coordinates
                           + "). Should be groupId:artifactId:[packaging]:[classifier]:[version]");
      }
      return builder;
   }

   public static CoordinateBuilder create(Map<String, String> atts)
   {
      CoordinateBuilder builder = CoordinateBuilder.create();
      builder.setGroupId(atts.get("groupId"));
      builder.setArtifactId(atts.get("artifactId"));
      builder.setVersion(atts.get("version"));
      builder.setClassifier(atts.get("classifier"));
      builder.setPackaging(atts.get("packaging"));
      return builder;
   }

   public static CoordinateBuilder create(Coordinate c)
   {
      CoordinateBuilder builder = create();
      builder.setGroupId(c.getGroupId()).setArtifactId(c.getArtifactId());
      builder.setClassifier(c.getClassifier()).setPackaging(c.getPackaging()).setVersion(c.getVersion());
      return builder;
   }

   public static CoordinateBuilder create()
   {
      return new CoordinateBuilder();
   }

   private CoordinateBuilder()
   {
   }

   @Override
   public String getGroupId()
   {
      return groupId;
   }

   @Override
   public String getArtifactId()
   {
      return artifactId;
   }

   @Override
   public String getVersion()
   {
      return version;
   }

   @Override
   public boolean isSnapshot()
   {
      return getVersion() != null && getVersion().endsWith("SNAPSHOT");
   }

   public CoordinateBuilder setGroupId(String groupId)
   {
      this.groupId = groupId;
      return this;
   }

   public CoordinateBuilder setArtifactId(String artifactId)
   {
      this.artifactId = artifactId;
      return this;
   }

   public CoordinateBuilder setVersion(String version)
   {
      this.version = version;
      return this;
   }

   @Override
   public String getClassifier()
   {
      return classifier;
   }

   public CoordinateBuilder setClassifier(String classifier)
   {
      this.classifier = classifier;
      return this;
   }

   @Override
   public String getPackaging()
   {
      return packaging;
   }

   @Override
   public String getSystemPath()
   {
      return systemPath;
   }

   public CoordinateBuilder setPackaging(String packaging)
   {
      this.packaging = packaging;
      return this;
   }

   public CoordinateBuilder setSystemPath(final String systemPath)
   {
      this.systemPath = systemPath;
      return this;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
      result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((packaging == null) ? 0 : packaging.hashCode());
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
      CoordinateBuilder other = (CoordinateBuilder) obj;
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
      if (groupId == null)
      {
         if (other.groupId != null)
            return false;
      }
      else if (!groupId.equals(other.groupId))
         return false;
      if (packaging == null)
      {
         if (other.packaging != null)
            return false;
      }
      else if (!packaging.equals(other.packaging))
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

   /**
    * Convenience method which should be used to convert a {@link Coordinate} object into its id representation, for
    * example: "groupId:artifactId:::version", "groupId:artifactId:packaging::version" or
    * "groupId:artifactId:packaging:classifier:version"
    */
   @Override
   public String toString()
   {
      StringBuilder gav = new StringBuilder(getGroupId()).append(":").append(getArtifactId());
      if (Strings.isNullOrEmpty(getClassifier())
               && (Strings.isNullOrEmpty(getPackaging()) || "jar".equalsIgnoreCase(getPackaging())))
      {
         gav.append(":");
         if (getVersion() != null)
         {
            gav.append(getVersion());
         }
      }
      else
      {
         gav.append(":");
         if (!Strings.isNullOrEmpty(getPackaging()))
         {
            gav.append(getPackaging());
         }
         if (!Strings.isNullOrEmpty(getClassifier()))
         {
            gav.append(":");
            gav.append(getClassifier());
         }
         gav.append(":");
         if (getVersion() != null)
         {
            gav.append(getVersion());
         }
      }
      return gav.toString();
   }

}