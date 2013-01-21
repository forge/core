/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.profiles;

import java.util.List;
import java.util.Properties;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

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
