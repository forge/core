/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.mvn.resources;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * MavenPomResourceTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class MavenPomResourceTest extends AbstractShellTest
{
   @Inject
   private ResourceFactory resourceFactory;

   @Test
   public void shouldBeAbleToGetChildrenForPom() throws Exception
   {
      MavenPomResource resource = resourceFactory.getResourceFrom(new File("pom.xml")).reify(MavenPomResource.class);
      List<Resource<?>> resources = resource.listResources();
      Assert.assertNotNull(resources);
      Assert.assertFalse(resources.isEmpty());
   }
}
