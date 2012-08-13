/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.profiles;

import org.apache.maven.model.Activation;
import org.apache.maven.model.Repository;
import org.jboss.forge.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

public class ProfileAdapter extends org.apache.maven.model.Profile
{
   private static final long serialVersionUID = 4863517832291256970L;

   public ProfileAdapter(final Profile profile)
   {
      setId(profile.getId());
      Activation activation = new Activation();
      activation.setActiveByDefault(profile.isActiveByDefault());

      setActivation(activation);

      for (Dependency dependency : profile.listDependencies())
      {
         getDependencies().add(new MavenDependencyAdapter(dependency));
      }

      for (DependencyRepository repository : profile.listRepositories())
      {
         Repository mavenRepository = new Repository();
         mavenRepository.setId(repository.getId());
         mavenRepository.setUrl(repository.getUrl());
         getRepositories().add(mavenRepository);
      }

      setProperties(profile.getProperties());
   }
}
