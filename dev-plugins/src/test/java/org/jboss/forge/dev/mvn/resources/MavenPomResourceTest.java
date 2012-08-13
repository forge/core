/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
