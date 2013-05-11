/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.projects.MavenFacet;
import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.projects.Project;
import org.jboss.forge.resource.FileResource;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParentProjectWorkspaceReader implements WorkspaceReader
{
   private final WorkspaceRepository repository = new WorkspaceRepository("forge");
   private Project project;

   public ParentProjectWorkspaceReader(Project project)
   {
      this.project = project;
   }

   @Override
   public File findArtifact(Artifact artifact)
   {
      MavenFacet maven = project.getFacet(MavenFacet.class);
      File result = findArtifact(artifact, new HashSet<MavenPomResource>(), maven.getPomResource());
      return result;
   }

   private File findArtifact(Artifact artifact, Set<MavenPomResource> seen, MavenPomResource pomResource)
   {
      if (seen.contains(pomResource))
         return null;
      else
         seen.add(pomResource);

      if (pomMatches(artifact, pomResource, true))
         return pomResource.getUnderlyingResourceObject();

      List<String> modules = pomResource.getCurrentModel().getModules();
      for (String module : modules)
      {
         MavenPomResource modulePom = project.getProjectRoot().getChild(module + "/" + "pom.xml")
                  .reify(MavenPomResource.class);

         if (modulePom.exists())
         {
            File modulePomFile = findArtifact(artifact, seen, modulePom);
            if (modulePomFile != null)
            {
               return modulePomFile;
            }
         }
      }

      Parent parent = pomResource.getCurrentModel().getParent();
      if (parent != null)
      {
         FileResource<?> temp = project.getProjectRoot();
         String relativePath = parent.getRelativePath();
         if (relativePath != null)
         {
            relativePath = relativePath.trim();
            while (relativePath.startsWith("."))
            {
               if (relativePath.startsWith(".."))
               {
                  relativePath = relativePath.replaceFirst("\\.\\.(\\\\|/)", "");
                  temp = temp.getParent();
               }
               else if (relativePath.startsWith("."))
               {
                  relativePath = relativePath.replaceFirst("\\.(\\\\|/)", "");
               }
            }
         }

         MavenPomResource parentPom = temp.getChild(relativePath).reify(MavenPomResource.class);

         if (parentPom.exists())
         {
            return findArtifact(artifact, seen, parentPom);
         }
      }

      return null;
   }

   private boolean pomMatches(Artifact artifact, MavenPomResource modulePom, boolean matchVersion)
   {
      Model pom = modulePom.getCurrentModel();
      String groupId = pom.getGroupId();
      if (groupId == null && pom.getParent() != null)
      {
         groupId = pom.getParent().getGroupId();
      }

      String artifactId = pom.getArtifactId();

      String version = pom.getVersion();
      if (version == null && pom.getParent() != null)
      {
         version = pom.getParent().getGroupId();
      }

      return (groupId != null && groupId.trim().equals(artifact.getGroupId().trim()))
               && (artifactId != null && artifactId.trim().equals(artifact.getArtifactId().trim()))
               && (!matchVersion || (version != null && version.trim().equals(artifact.getVersion().trim())));
   }

   @Override
   public List<String> findVersions(Artifact artifact)
   {
      MavenFacet maven = project.getFacet(MavenFacet.class);
      List<String> result = findVersions(artifact, new HashSet<MavenPomResource>(), maven.getPomResource());
      return result;
   }

   private List<String> findVersions(Artifact artifact, HashSet<MavenPomResource> seen, MavenPomResource pomResource)
   {
      List<String> result = new ArrayList<String>();
      if (seen.contains(pomResource))
         return null;
      else
         seen.add(pomResource);

      if (pomMatches(artifact, pomResource, false))
      {
         Model model = pomResource.getCurrentModel();
         String version = model.getVersion();
         if (version == null && model.getParent() != null)
         {
            version = model.getParent().getVersion();
         }
         if (result != null)
            result.add(version);
      }

      List<String> modules = pomResource.getCurrentModel().getModules();
      for (String module : modules)
      {
         MavenPomResource modulePom = project.getProjectRoot().getChild(module + "/" + "pom.xml")
                  .reify(MavenPomResource.class);

         if (modulePom.exists())
         {
            result.addAll(findVersions(artifact, seen, modulePom));
         }
      }

      Parent parent = pomResource.getCurrentModel().getParent();
      if (parent != null)
      {
         FileResource<?> temp = project.getProjectRoot();
         String relativePath = parent.getRelativePath();
         if (relativePath != null)
         {
            relativePath = relativePath.trim();
            while (relativePath.startsWith("."))
            {
               if (relativePath.startsWith(".."))
               {
                  relativePath = relativePath.replaceFirst("\\.\\.(\\\\|/)", "");
                  temp = temp.getParent();
               }
               else if (relativePath.startsWith("."))
               {
                  relativePath = relativePath.replaceFirst("\\.(\\\\|/)", "");
               }
            }
         }

         MavenPomResource parentPom = temp.getChild(relativePath).reify(MavenPomResource.class);

         if (parentPom.exists())
         {
            result.addAll(findVersions(artifact, seen, parentPom));
         }
      }

      return result;
   }

   public List<MavenPomResource> gatherPoms()
   {
      HashSet<MavenPomResource> seen = new HashSet<MavenPomResource>();
      gatherPoms(seen, project.getFacet(MavenFacet.class).getPomResource());
      return new ArrayList<MavenPomResource>(seen);
   }

   private void gatherPoms(Set<MavenPomResource> seen, MavenPomResource pomResource)
   {
      if (!seen.contains(pomResource))
      {
         seen.add(pomResource);

         List<String> modules = pomResource.getCurrentModel().getModules();
         for (String module : modules)
         {
            MavenPomResource modulePom = project.getProjectRoot().getChild(module + "/" + "pom.xml")
                     .reify(MavenPomResource.class);

            if (modulePom.exists())
            {
               gatherPoms(seen, modulePom);
            }
         }

         Parent parent = pomResource.getCurrentModel().getParent();
         if (parent != null)
         {
            FileResource<?> temp = project.getProjectRoot();
            String relativePath = parent.getRelativePath();
            if (relativePath != null)
            {
               relativePath = relativePath.trim();
               while (relativePath.startsWith("."))
               {
                  if (relativePath.startsWith(".."))
                  {
                     relativePath = relativePath.replaceFirst("\\.\\.(\\\\|/)", "");
                     temp = temp.getParent();
                  }
                  else if (relativePath.startsWith("."))
                  {
                     relativePath = relativePath.replaceFirst("\\.(\\\\|/)", "");
                  }
               }
            }

            MavenPomResource parentPom = temp.getChild(relativePath).reify(MavenPomResource.class);

            if (parentPom.exists())
            {
               gatherPoms(seen, parentPom);
            }
         }
      }
   }

   public MavenPomResource getParentPom()
   {
      return getParentPom(project.getFacet(MavenFacet.class).getPomResource());
   }

   private MavenPomResource getParentPom(MavenPomResource pomResource)
   {
      Parent parent = pomResource.getCurrentModel().getParent();
      if (parent != null)
      {
         FileResource<?> temp = project.getProjectRoot();
         String relativePath = parent.getRelativePath();
         if (relativePath != null)
         {
            relativePath = relativePath.trim();
            while (relativePath.startsWith("."))
            {
               if (relativePath.startsWith(".."))
               {
                  relativePath = relativePath.replaceFirst("\\.\\.(\\\\|/)", "");
                  temp = temp.getParent();
               }
               else if (relativePath.startsWith("."))
               {
                  relativePath = relativePath.replaceFirst("\\.(\\\\|/)", "");
               }
            }
         }

         MavenPomResource parentPom = temp.getChild(relativePath).reify(MavenPomResource.class);

         if (parentPom.exists())
         {
            return getParentPom(parentPom);
         }
      }
      return pomResource;
   }

   @Override
   public WorkspaceRepository getRepository()
   {
      return repository;
   }

}