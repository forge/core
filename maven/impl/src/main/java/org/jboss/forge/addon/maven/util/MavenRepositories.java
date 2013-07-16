/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.util;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.settings.Settings;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.maven.MavenContainer;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MavenRepositories
{
   private static final String MAVEN_CENTRAL_REPO = "http://repo1.maven.org/maven2";

   public static List<RemoteRepository> getRemoteRepositories(MavenContainer container, Settings settings)
   {
      List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(
               Arrays.asList(new DependencyRepository("central", MAVEN_CENTRAL_REPO)), settings);
      remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));
      return remoteRepos;
   }

   public static List<RemoteRepository> getRemoteRepositories(MavenContainer container, Settings settings,
            DependencyQuery query)
   {
      List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(query.getDependencyRepositories(),
               settings);

      if (remoteRepos.isEmpty())
      {
         remoteRepos = MavenConvertUtils.convertToMavenRepos(
                  Arrays.asList(new DependencyRepository("central", MAVEN_CENTRAL_REPO)), settings);
      }

      remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));
      return remoteRepos;
   }
}
