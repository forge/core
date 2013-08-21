/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.rest.RootAndNestedDtoGenerator;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JpaDtoGeneratorTest extends AbstractJPATest
{

   @Inject
   private RootAndNestedDtoGenerator jpaDtoGenerator;

   @Test
   public void testCreateDTOBasedEndpoint() throws Exception
   {
      Project project = getProject();
      setupRest();

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass userEntity = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("User.java"));
      userEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(userEntity);

      jpaDtoGenerator.from(userEntity, java.getBasePackage() + ".rest.dto");

      JavaResource dtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.UserDTO");
      JavaClass dto = (JavaClass) dtoResource.getJavaSource();
      assertNotNull(dto.getField("id"));
      assertEquals("Long", dto.getField("id").getType());
      assertNotNull(dto.getMethod("getId"));
      assertNotNull(dto.getMethod("setId", "Long"));
      assertNotNull(dto.getField("version"));
      assertEquals("int", dto.getField("version").getType());
      assertNotNull(dto.getMethod("getVersion"));
      assertNotNull(dto.getMethod("setVersion", "int"));

      getShell().execute("build");
   }

   @Test
   public void testCreateDTOBasedEndpointWithAssociation() throws Exception
   {
      Project project = getProject();
      setupRest();

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass parentEntity = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("Parent.java"));
      parentEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(parentEntity);
      JavaClass childEntity = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("Child.java"));
      childEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(childEntity);
      JavaClass embeddableProperty = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("EmbeddableProperty.java"));
      embeddableProperty.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(embeddableProperty);
      JavaClass embeddableAssociation = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("AssociationInEmbeddable.java"));
      embeddableAssociation.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(embeddableAssociation);
      JavaClass association = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("Association.java"));
      association.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(association);

      getShell().setCurrentResource(java.getJavaResource(parentEntity));

      jpaDtoGenerator.from(parentEntity, java.getBasePackage() + ".rest.dto");

      JavaResource parentDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.ParentDTO");
      JavaClass parentDto = (JavaClass) parentDtoResource.getJavaSource();
      // Verify that the ordinary properties of root DTOs are created
      assertNotNull(parentDto.getField("objectId"));
      assertEquals("Long", parentDto.getField("objectId").getType());
      assertNotNull(parentDto.getMethod("getObjectId"));
      assertNotNull(parentDto.getMethod("setObjectId", "Long"));
      assertNotNull(parentDto.getField("version"));
      assertEquals("int", parentDto.getField("version").getType());
      assertNotNull(parentDto.getMethod("getVersion"));
      assertNotNull(parentDto.getMethod("setVersion", "int"));
      // Verify that the association properties of root DTOs are created referenced nested DTOs
      assertNotNull(parentDto.getField("children"));
      assertEquals("Set", parentDto.getField("children").getType());
      assertEquals("NestedChildDTO", parentDto.getField("children").getTypeInspector().getTypeArguments().get(0).getName());
      assertNotNull(parentDto.getMethod("getChildren"));
      assertNotNull(parentDto.getMethod("setChildren", "Set"));
      assertNotNull(parentDto.getField("association"));
      assertEquals("NestedAssociationDTO", parentDto.getField("association").getType());
      assertNotNull(parentDto.getMethod("getAssociation"));
      assertNotNull(parentDto.getMethod("setAssociation", "NestedAssociationDTO"));
      // Verify that embeddedable properties are created as top level DTOs
      assertEquals("EmbeddablePropertyDTO", parentDto.getField("embeddable").getType());
      assertNotNull(parentDto.getMethod("getEmbeddable"));
      assertNotNull(parentDto.getMethod("setEmbeddable", "EmbeddablePropertyDTO"));
      
      
      JavaResource nestedChildDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.NestedChildDTO");
      JavaClass nestedChildDto = (JavaClass) nestedChildDtoResource.getJavaSource();
      // Verify that the ordinary properties of nested DTOs are created
      assertNotNull(nestedChildDto.getField("id"));
      assertEquals("Long", nestedChildDto.getField("id").getType());
      assertNotNull(nestedChildDto.getMethod("getId"));
      assertNotNull(nestedChildDto.getMethod("setId", "Long"));
      assertNotNull(nestedChildDto.getField("version"));
      assertEquals("int", nestedChildDto.getField("version").getType());
      assertNotNull(nestedChildDto.getMethod("getVersion"));
      assertNotNull(nestedChildDto.getMethod("setVersion", "int"));
      // Verify that embeddedable properties are created as top level DTOs
      assertEquals("EmbeddablePropertyDTO", nestedChildDto.getField("embeddable").getType());
      assertNotNull(nestedChildDto.getMethod("getEmbeddable"));
      assertNotNull(nestedChildDto.getMethod("setEmbeddable", "EmbeddablePropertyDTO"));
      // Verify that the association properties of nested DTOs are not created
      assertNull(nestedChildDto.getField("association"));
      
      
      JavaResource embeddablePropertyDtoResource = java.getJavaResource(java.getBasePackage()
               + ".rest.dto.EmbeddablePropertyDTO");
      JavaClass embeddablePropertyDto = (JavaClass) embeddablePropertyDtoResource.getJavaSource();
      // Verify that the ordinary properties of Embeddables are created
      assertNotNull(embeddablePropertyDto.getField("attrA"));
      assertEquals("String", embeddablePropertyDto.getField("attrA").getType());
      assertNotNull(embeddablePropertyDto.getMethod("getAttrA"));
      assertNotNull(embeddablePropertyDto.getMethod("setAttrA", "String"));
      assertNotNull(embeddablePropertyDto.getField("attrB"));
      assertEquals("String", embeddablePropertyDto.getField("attrB").getType());
      assertNotNull(embeddablePropertyDto.getMethod("getAttrB"));
      assertNotNull(embeddablePropertyDto.getMethod("setAttrB", "String"));
      // Verify that the association properties of Embeddables are created via new nested DTOs
      assertNotNull(embeddablePropertyDto.getField("associationInEmbeddable"));
      assertEquals("NestedAssociationInEmbeddableDTO", embeddablePropertyDto.getField("associationInEmbeddable").getType());
      assertNotNull(embeddablePropertyDto.getMethod("getAssociationInEmbeddable"));
      assertNotNull(embeddablePropertyDto.getMethod("setAssociationInEmbeddable", "NestedAssociationInEmbeddableDTO"));
      
      
      JavaResource nestedAssociationDtoResource = java.getJavaResource(java.getBasePackage()
               + ".rest.dto.NestedAssociationDTO");
      JavaClass nestedAssociationDto = (JavaClass) nestedAssociationDtoResource.getJavaSource();
      // Verify that the ordinary properties are created in the nested DTO
      assertNotNull(nestedAssociationDto.getField("id"));
      assertEquals("Long", nestedAssociationDto.getField("id").getType());
      assertNotNull(nestedAssociationDto.getMethod("getId"));
      assertNotNull(nestedAssociationDto.getMethod("setId", "Long"));
      assertNotNull(nestedAssociationDto.getField("version"));
      assertEquals("int", nestedAssociationDto.getField("version").getType());
      assertNotNull(nestedAssociationDto.getMethod("getVersion"));
      assertNotNull(nestedAssociationDto.getMethod("setVersion", "int"));
      // Verify that the association properties are not created in the nested DTO
      assertNull(nestedAssociationDto.getField("parent"));
      
      
      JavaResource nestedAssociationInEmbeddableDtoResource = java.getJavaResource(java.getBasePackage()
               + ".rest.dto.NestedAssociationInEmbeddableDTO");
      JavaClass nestedAssociationInEmbeddableDto = (JavaClass) nestedAssociationInEmbeddableDtoResource.getJavaSource();
      // Verify that the ordinary properties are created in the nested DTO
      assertNotNull(nestedAssociationInEmbeddableDto.getField("id"));
      assertEquals("Long", nestedAssociationInEmbeddableDto.getField("id").getType());
      assertNotNull(nestedAssociationInEmbeddableDto.getMethod("getId"));
      assertNotNull(nestedAssociationInEmbeddableDto.getMethod("setId", "Long"));
      assertNotNull(nestedAssociationInEmbeddableDto.getField("version"));
      assertEquals("int", nestedAssociationInEmbeddableDto.getField("version").getType());
      assertNotNull(nestedAssociationInEmbeddableDto.getMethod("getVersion"));
      assertNotNull(nestedAssociationInEmbeddableDto.getMethod("setVersion", "int"));
      // Verify that the association properties are not created in the nested DTO
      assertNull(nestedAssociationInEmbeddableDto.getField("parent"));

      getShell().execute("build");
   }

   private void setupRest() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup rest");
   }
}
