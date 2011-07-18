/*
 * JBoss, by Red Hat.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
