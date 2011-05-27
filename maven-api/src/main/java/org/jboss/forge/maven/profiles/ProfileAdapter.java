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

import org.apache.maven.model.Activation;
import org.apache.maven.model.Repository;
import org.jboss.forge.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

public class ProfileAdapter extends org.apache.maven.model.Profile
{

   public ProfileAdapter(Profile profile)
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
