/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.profiles;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.junit.Test;

public class ProfileAdapterTest
{
   @Test
   public void testCreateFromProfile()
   {
      ProfileBuilder profileBuilder =
              ProfileBuilder.create()
                      .setId("myid")
                      .setActiveByDefault(true)
                      .addDependency(DependencyBuilder.create("mygroupId:myartifactId"))
                      .addDependency(DependencyBuilder.create("mygroupId:mysecond"))
                      .addRepository(new DependencyRepositoryImpl("id", "url"));

      ProfileAdapter profileAdapter = new ProfileAdapter(profileBuilder);
      assertThat(profileAdapter.getId(), is(profileBuilder.getId()));
      assertThat(profileAdapter.getActivation().isActiveByDefault(), is(true));
      assertThat(profileAdapter.getDependencies().size(), is(2));
      assertThat(profileAdapter.getRepositories().size(), is(1));
   }
}
