/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.cdi.ui.BeanScope;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacesOperationsTest
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private FacesOperations operations;

   @Test
   public void testCreateBackingBean() throws Exception
   {
      JavaClassSource source = Roaster.create(JavaClassSource.class);
      source = operations.newBackingBean(source, BeanScope.DEPENDENT);
      Assert.assertTrue(source.hasAnnotation(Named.class));
   }

   @Test
   public void testCreateConverter() throws Exception
   {
      JavaClassSource source = Roaster.create(JavaClassSource.class);
      source = operations.newConverter(source);
      assertTrue(source.hasAnnotation(FacesConverter.class));
      assertTrue(source.hasInterface(Converter.class));
      assertEquals(0, source.getFields().size());
      assertEquals(2, source.getMethods().size());
   }

   @Test
   public void testCreateValidator() throws Exception
   {
      JavaClassSource source = Roaster.create(JavaClassSource.class);
      source = operations.newValidator(source, "name", "package");
      assertTrue(source.hasAnnotation(FacesValidator.class));
      assertTrue(source.hasInterface(Validator.class));
      assertEquals(0, source.getFields().size());
      assertEquals(1, source.getMethods().size());
   }

   @Test
   public void testCreateValidatorMethod() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);

      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = sourceFacet
               .saveJavaSource(Roaster.parse(JavaClassSource.class, "package org.example; public class DemoBean {}"));

      MethodSource<?> method = operations.addValidatorMethod(resource, "validateUsername");
      Assert.assertEquals(3, method.getParameters().size());

      JavaClassSource source = resource.getJavaType();
      Assert.assertEquals(1, source.getMethods().size());
      Assert.assertEquals(method.toSignature(), source.getMethods().get(0).toSignature());
   }
}
