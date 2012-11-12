/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.util.Map;

public class CoordinateBuilder implements Coordinate
{
   private String groupId;
   private String artifactId;
   private String version;
   private String classifier;
   private String packaging;

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

   public String getGroupId()
   {
      return groupId;
   }

   public String getArtifactId()
   {
      return artifactId;
   }

   public String getVersion()
   {
      return version;
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

   public String getClassifier()
   {
      return classifier;
   }

   public CoordinateBuilder setClassifier(String classifier)
   {
      this.classifier = classifier;
      return this;
   }

   public String getPackaging()
   {
      return packaging;
   }

   public CoordinateBuilder setPackaging(String packaging)
   {
      this.packaging = packaging;
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

   @Override
   public String toString()
   {
      return "CoordinateBuilder [" + (groupId != null ? "groupId=" + groupId + ", " : "")
               + (artifactId != null ? "artifactId=" + artifactId + ", " : "")
               + (version != null ? "version=" + version + ", " : "")
               + (classifier != null ? "classifier=" + classifier + ", " : "")
               + (packaging != null ? "packaging=" + packaging : "") + "]";
   }

}
