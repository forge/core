/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.util;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;

/**
 * @author Mike Brock .
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PluginRef
{
   private String name = "";
   private String website = "";
   private String author = "";
   private String description = "";
   private String tags = "";
   private String artifact = "";
   private String gitRepo = "";
   private String homeRepo = "";
   private String gitRef = "";

   public String getGitRef()
   {
      return gitRef;
   }

   public String getName()
   {
      return name == null ? "" : name;
   }

   public String getAuthor()
   {
      return author == null ? "" : author;
   }

   public String getDescription()
   {
      return description == null ? "" : description;
   }

   public String getTags()
   {
      return tags == null ? "" : tags;
   }

   public Dependency getArtifact()
   {
      return DependencyBuilder.create(artifact);
   }

   public String getHomeRepo()
   {
      return homeRepo == null ? "" : homeRepo;
   }

   public String getGitRepo()
   {
      return gitRepo == null ? "" : gitRepo;
   }

   public boolean isGit()
   {
      return !Strings.isNullOrEmpty(gitRepo);
   }

   public String getWebsite()
   {
      return website == null ? "" : website;
   }

   public String getLocation()
   {
      String location = getHomeRepo();
      if (Strings.isNullOrEmpty(location))
      {
         location = getGitRepo();
      }
      if (Strings.isNullOrEmpty(location))
      {
         location = "Unknown";
      }
      return location;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   public void setWebsite(final String website)
   {
      this.website = website;
   }

   public void setAuthor(final String author)
   {
      this.author = author;
   }

   public void setDescription(final String description)
   {
      this.description = description;
   }

   public void setTags(final String tags)
   {
      this.tags = tags;
   }

   public void setArtifact(final String artifact)
   {
      this.artifact = artifact;
   }

   public void setGitRepo(final String gitRepo)
   {
      this.gitRepo = gitRepo;
   }

   public void setHomeRepo(final String homeRepo)
   {
      this.homeRepo = homeRepo;
   }

   public void setGitRef(final String gitRef)
   {
      this.gitRef = gitRef;
   }
}
