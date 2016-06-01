/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.profiles;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.junit.Test;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
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
                        .addRepository(new DependencyRepository("id", "url"));
      ProfileAdapter profileAdapter = new ProfileAdapter(profileBuilder);
      assertThat(profileAdapter.getId(), equalTo(profileBuilder.getId()));
      assertThat(profileAdapter.getActivation().isActiveByDefault(), is(true));
      assertThat(profileAdapter.getDependencies().size(), is(2));
      assertThat(profileAdapter.getRepositories().size(), is(1));
   }
}
