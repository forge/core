/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin.project;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Profile;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
public class ProfileCompleter extends SimpleTokenCompleter
{

   @Inject
   private Project project;

   @Override
   public List<String> getCompletionTokens()
   {
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      List<String> profiles = new ArrayList<String>();
      List<Profile> profileList = mavenCoreFacet.getPOM().getProfiles();
      for (Profile profile : profileList)
      {
         profiles.add(profile.getId());
      }

      return profiles;
   }
}
