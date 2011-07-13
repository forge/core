package org.jboss.forge.test;

import java.io.File;

/**
 * Crude maven artifact resolver.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MavenArtifactResolver
{
   private static final String LOCAL_MAVEN_REPO =
            System.getProperty("maven.repo.local") != null ?
                     System.getProperty("maven.repo.local") :
                     (System.getProperty("user.home") + File.separatorChar +
                              ".m2" + File.separatorChar + "repository");

   public static File resolve(final String groupId, final String artifactId,
            final String version)
   {
      return resolve(groupId, artifactId, version, null);
   }

   public static File resolve(final String groupId, final String artifactId,
            final String version, final String classifier)
   {
      return new File(LOCAL_MAVEN_REPO + File.separatorChar +
               groupId.replace(".", File.separator) + File.separatorChar +
               artifactId + File.separatorChar +
               version + File.separatorChar +
               artifactId + "-" + version +
               (classifier != null ? ("-" + classifier) : "") + ".jar");
   }

   public static File resolve(final String qualifiedArtifactId)
   {
      String[] segments = qualifiedArtifactId.split(":");
      if (segments.length == 3)
      {
         return resolve(segments[0], segments[1], segments[2]);
      }
      else if (segments.length == 4)
      {
         return resolve(segments[0], segments[1], segments[2], segments[3]);
      }
      throw new IllegalArgumentException("Invalid qualified artifactId syntax: " +
               qualifiedArtifactId);
   }

   public static File[] resolve(final String... qualifiedArtifactIds)
   {
      int n = qualifiedArtifactIds.length;
      File[] artifacts = new File[n];
      for (int i = 0; i < n; i++)
      {
         artifacts[i] = resolve(qualifiedArtifactIds[i]);
      }

      return artifacts;
   }
}
