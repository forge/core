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

import org.apache.maven.model.Profile;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
