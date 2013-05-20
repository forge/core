/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.addon.projects.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.jboss.forge.maven.addon.projects.util.RepositoryUtils;
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
   public void testConvertFromMavenProxyWithoutAuth()
   {
      Proxy proxySettings = new Proxy();
      proxySettings.setHost("foo.com");
      proxySettings.setPort(3128);
      org.sonatype.aether.repository.Proxy proxyObj = RepositoryUtils.convertFromMavenProxy(proxySettings);
      assertNotNull(proxyObj);
      assertEquals(proxySettings.getHost(), proxyObj.getHost());
      assertEquals(proxySettings.getPort(), proxyObj.getPort());
      assertNull(proxyObj.getAuthentication().getUsername());
      assertNull(proxyObj.getAuthentication().getPassword());
   }

   @Test
   public void testConvertFromMavenProxyWithAuth()
   {
      Proxy proxySettings = new Proxy();
      proxySettings.setHost("foo.com");
      proxySettings.setPort(3128);
      proxySettings.setUsername("john");
      proxySettings.setPassword("doe");
      org.sonatype.aether.repository.Proxy proxyObj = RepositoryUtils.convertFromMavenProxy(proxySettings);
      assertNotNull(proxyObj);
      assertEquals(proxySettings.getHost(), proxyObj.getHost());
      assertEquals(proxySettings.getPort(), proxyObj.getPort());
      assertEquals(proxySettings.getUsername(), proxyObj.getAuthentication().getUsername());
      assertEquals(proxySettings.getPassword(), proxyObj.getAuthentication().getPassword());
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
