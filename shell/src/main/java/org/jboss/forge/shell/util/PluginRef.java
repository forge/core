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
   private String artifact = "";
   private String gitRepo = "";
   private String homeRepo = "";
   private String gitRef = "";

   public PluginRef(final String name, final String website, final String author, final String description,
            final String artifact,
            final String homeRepo, final String gitRepo,
            final String gitRef)
   {
      this.name = name;
      this.website = website;
      this.author = author;
      this.description = description;
      this.artifact = artifact;
      this.homeRepo = homeRepo;
      this.gitRepo = gitRepo;
      this.gitRef = gitRef;
   }

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
      return gitRepo == null ? "" : website;
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
}
