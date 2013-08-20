/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.plugin;

import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

/**
 * @author <a href="mailto:salmon.charles@gmail.com">charless</a>
 */
@RunWith(Arquillian.class)
public class RestPluginEventsTest extends AbstractJPATest
{   
   @Inject
   private RestGeneratedResourcesEventObserver observer;

   @Test
   public void testCreateEndpointObservesRestGeneratedResourcesEvent() throws Exception
   {
      Project project = getProject();
      JavaClass entity = generateEntity(project, null, "User");
      assertFalse(entity.hasAnnotation(XmlRootElement.class));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaResource dto = java.getJavaResource(java.getBasePackage() + ".rest.dto.UserDTO");

      List<JavaResource> endpoints = observer.getEndpoints();
      Assert.assertEquals(1, endpoints.size());
      Assert.assertEquals(resource.getFullyQualifiedName(), endpoints.get(0).getFullyQualifiedName());

      List<JavaResource> entities = observer.getEntities();
      Assert.assertEquals(1, entities.size());
      JavaResource resourceEntity = java.getJavaResource(entity.getCanonicalName());
      Assert.assertEquals(resourceEntity.getFullyQualifiedName(), entities.get(0).getFullyQualifiedName());

      List<JavaResource> dtos = observer.getDtos();
      Assert.assertEquals(1, dtos.size());
      Assert.assertEquals(dto.getFullyQualifiedName(), dtos.get(0).getFullyQualifiedName());
   }

   private void setupRest() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup rest");
   }
}
