/*
 *
 *  * JBoss, Home of Professional Open Source
 *  * Copyright 2011, Red Hat, Inc., and individual contributors
 *  * by the @authors tag. See the copyright.txt in the distribution for a
 *  * full listing of individual contributors.
 *  *
 *  * This is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU Lesser General Public License as
 *  * published by the Free Software Foundation; either version 2.1 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This software is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this software; if not, write to the Free
 *  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.jboss.forge.maven.profiles;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

import java.util.List;
import java.util.Properties;

public class ProfileBuilder implements Profile
{
   private ProfileImpl profile;

   private ProfileBuilder(ProfileImpl profile)
   {
      this.profile = profile;
   }

   public static ProfileBuilder create()
   {
      return new ProfileBuilder(new ProfileImpl());
   }

   public static ProfileBuilder create(Profile profile)
   {
      if (profile instanceof ProfileImpl)
      {
         return new ProfileBuilder((ProfileImpl) profile);
      } else if (profile instanceof ProfileBuilder)
      {
         return new ProfileBuilder(((ProfileBuilder) profile).profile);
      }

      throw new IllegalArgumentException("Profile of type '" + profile.getClass().getName() + "' is not supported");
   }

   @Override public String getId()
   {
      return profile.getId();
   }

   @Override public boolean isActiveByDefault()
   {
      return profile.isActiveByDefault();
   }

   @Override public List<Dependency> listDependencies()
   {
      return profile.listDependencies();
   }

   @Override public List<DependencyRepository> listRepositories()
   {
      return profile.listRepositories();
   }

   @Override public Properties getProperties()
   {
      return profile.getProperties();
   }

   public ProfileBuilder setId(String id)
   {
      profile.setId(id);
      return this;
   }

   public ProfileBuilder setActiveByDefault(boolean activeByDefault)
   {
      profile.setActivateByDefault(activeByDefault);
      return this;
   }

   public ProfileBuilder addDependency(Dependency dependency)
   {
      profile.listDependencies().add(dependency);
      return this;
   }

   public ProfileBuilder addRepository(DependencyRepository repository)
   {
      profile.listRepositories().add(repository);
      return this;
   }

   public ProfileBuilder addProperty(String key, String value)
   {
      profile.getProperties().setProperty(key, value);
      return this;
   }

   public org.apache.maven.model.Profile getAsMavenProfile()
   {
      return new ProfileAdapter(profile);
   }
}
