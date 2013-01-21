/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.profiles;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.apache.maven.model.Profile;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.junit.Test;

public class ProfileBuilderTest
{
   @Test
   public void testCreateWithExistingProfile() throws Exception
   {
      ProfileImpl profile = new ProfileImpl();
      profile.setId("testprofile");

      ProfileBuilder profileBuilder = ProfileBuilder.create(profile);
      assertThat(profileBuilder.getId(), is(profile.getId()));
   }

   @Test
   public void testCreate()
   {
      ProfileBuilder profileBuilder = ProfileBuilder.create();
      assertNotNull(profileBuilder);
   }

   @Test
   public void testMethodChaining()
   {
      ProfileBuilder profileBuilder =
              ProfileBuilder.create()
                      .setId("myid")
                      .setActiveByDefault(true)
                      .addDependency(DependencyBuilder.create("mygroupId:myartifactId"))
                      .addRepository(new DependencyRepositoryImpl("id", "url"));

      assertTrue(profileBuilder.isActiveByDefault());
   }

   @Test
   public void testAsMavenProfile()
   {
      ProfileBuilder profileBuilder =
              ProfileBuilder.create()
                      .setId("myid")
                      .setActiveByDefault(true)
                      .addDependency(DependencyBuilder.create("mygroupId:myartifactId"))
                      .addRepository(new DependencyRepositoryImpl("id", "url"));
      Profile mavenProfile = profileBuilder.getAsMavenProfile();
      assertThat(mavenProfile.getId(), is(profileBuilder.getId()));
   }

   @Test
   public void testAddProperty()
   {
      ProfileBuilder profileBuilder =
              ProfileBuilder.create()
                      .addProperty("prop1", "val1")
                      .addProperty("prop2", "val2")
                      .addProperty("prop3", "prop3");

      Profile mavenProfile = profileBuilder.getAsMavenProfile();
      assertThat(mavenProfile.getProperties().size(), is(3));
   }
}
