/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.rest.JpaDtoGenerator;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JpaDtoGeneratorTest extends AbstractJPATest
{

   @Inject
   private JpaDtoGenerator jpaDtoGenerator;

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
      assertNotNull(dto.getField("objectId"));
      assertEquals("Long", dto.getField("objectId").getType());
      assertNotNull(dto.getMethod("getObjectId"));
      assertNotNull(dto.getMethod("setObjectId", "Long"));
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
      JavaClass userEntity = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("User.java"));
      userEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(userEntity);
      JavaClass groupEntity = JavaParser.parse(JavaClass.class,
               JpaDtoGeneratorTest.class.getResourceAsStream("Group.java"));
      groupEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(groupEntity);

      getShell().setCurrentResource(java.getJavaResource(groupEntity));

      jpaDtoGenerator.from(groupEntity, java.getBasePackage() + ".rest.dto");

      JavaResource groupDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.GroupDTO");
      JavaClass groupDto = (JavaClass) groupDtoResource.getJavaSource();
      assertNotNull(groupDto.getField("objectId"));
      assertEquals("Long", groupDto.getField("objectId").getType());
      assertNotNull(groupDto.getMethod("getObjectId"));
      assertNotNull(groupDto.getMethod("setObjectId", "Long"));
      assertNotNull(groupDto.getField("version"));
      assertEquals("int", groupDto.getField("version").getType());
      assertNotNull(groupDto.getMethod("getVersion"));
      assertNotNull(groupDto.getMethod("setVersion", "int"));
      assertNotNull(groupDto.getField("users"));
      assertEquals("Set", groupDto.getField("users").getType());
      assertNotNull(groupDto.getMethod("getUsers"));
      assertNotNull(groupDto.getMethod("setUsers", "Set"));

      JavaResource nestedUserDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.NestedUserDTO");
      JavaClass nestedUserDto = (JavaClass) nestedUserDtoResource.getJavaSource();
      assertNotNull(nestedUserDto.getField("objectId"));
      assertEquals("Long", nestedUserDto.getField("objectId").getType());
      assertNotNull(nestedUserDto.getMethod("getObjectId"));
      assertNotNull(nestedUserDto.getMethod("setObjectId", "Long"));
      assertNotNull(nestedUserDto.getField("version"));
      assertEquals("int", nestedUserDto.getField("version").getType());
      assertNotNull(nestedUserDto.getMethod("getVersion"));
      assertNotNull(nestedUserDto.getMethod("setVersion", "int"));

      getShell().execute("build");
   }

   private void setupRest() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup rest");
   }
}
