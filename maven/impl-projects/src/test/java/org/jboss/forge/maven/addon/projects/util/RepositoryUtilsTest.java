/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.addon.projects.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.settings.Repository;
import org.jboss.forge.addon.maven.projects.util.RepositoryUtils;
import org.junit.Test;

/**
 * Test case for {@link RepositoryUtils} class
 * 
 * @author George Gastaldi <gegastaldi@gmail.com>
 * 
 */
public class RepositoryUtilsTest
{
   @Test
   public void testConvertFromMavenProxyExpectNull()
   {
      assertNull(RepositoryUtils.convertFromMavenProxy(null));
   }

   @Test
   public void testRepositoryP2Support()
   {
      Repository repository = new Repository();
      repository.setLayout("p2");
      repository.setId("swtbot");
      repository.setName("swtbot-nightly-staging-site");
      repository.setUrl("http://download.eclipse.org/technology/swtbot/snapshots");
      ArtifactRepository mavenRepo = RepositoryUtils.convertFromMavenSettingsRepository(repository);
      assertNotNull(mavenRepo);
      assertNotNull(mavenRepo.getLayout());
      assertEquals("p2", mavenRepo.getLayout().getId());
   }
}
