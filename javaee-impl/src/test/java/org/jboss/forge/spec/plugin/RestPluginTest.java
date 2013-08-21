/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.RestApplicationFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.RestWebXmlFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.spec.javaee.jpa.PersistencePlugin;
import org.jboss.forge.spec.javaee.rest.RestWebXmlFacetImpl;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.TransactionType;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RestPluginTest extends AbstractJPATest
{
   private static final DependencyBuilder JAX_RS_DEPENDENCY = DependencyBuilder
            .create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec");

   @Test
   public void testInstall() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec")));

      RestFacet restFacet = project.getFacet(RestFacet.class);
      assertEquals("/rest", restFacet.getApplicationPath());

      assertTrue(project.hasFacet(RestWebXmlFacet.class));
      RestWebXmlFacet restWebXmlFacet = project.getFacet(RestWebXmlFacet.class);
      assertEquals("/rest/*", restWebXmlFacet.getServletPath());
   }

   @Test
   public void testInstallAfterOtherServletMapping() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));

      queueInputLines("");
      getShell().execute("setup servlet");
      ServletFacet web = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = web.getConfig();
      config.facesServlet();
      web.saveConfig(config);

      org.jboss.forge.parser.xml.Node webXml = XMLParser.parse(web.getConfig().exportAsString());
      assertEquals(1, webXml.get("servlet-mapping").size());

      assertTrue(config.exportAsString().contains("servlet-mapping"));

      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec")));

      RestFacet restFacet = project.getFacet(RestFacet.class);
      assertEquals("/rest", restFacet.getApplicationPath());

      assertTrue(project.hasFacet(RestWebXmlFacet.class));
      RestWebXmlFacet restWebXmlFacet = project.getFacet(RestWebXmlFacet.class);
      assertEquals("/rest/*", restWebXmlFacet.getServletPath());

      webXml = XMLParser.parse(web.getConfig().exportAsString());
      assertEquals(2, webXml.get("servlet-mapping").size());
   }

   @Test
   public void testInstallWithApplicationClass() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      queueInputLines("", "", "demo", "demo", "RestApplication");
      getShell().execute("rest setup --activatorType APP_CLASS");

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               JAX_RS_DEPENDENCY));

      assertTrue(project.hasFacet(RestFacet.class));
      RestApplicationFacet restApplicationFacet = project.getFacet(RestApplicationFacet.class);
      assertNotNull(restApplicationFacet);
   }

   @Test
   public void testCreateEndpoint() throws Exception
   {
      Project project = getProject();
      generateEntity(project, null, "User");

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/users", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               endpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertEquals("java.util.List", endpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> method = endpoint.getMethod("findById", Long.class);
      Type<JavaClass> returnTypeInspector = method.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector
                        .getQualifiedName());

      getShell().execute("build");
   }

   @Test
   public void testCreateEndpointNonStandardId() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass entity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User.java"));
      entity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(entity);

      getShell().setCurrentResource(java.getJavaResource(entity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/users", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               endpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      getShell().execute("build");
   }

   @Test
   public void testCreateEndpointPrimitiveNonStandardId() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass entity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User2.java"));
      entity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(entity);

      getShell().setCurrentResource(java.getJavaResource(entity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.User2Endpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/user2s", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               endpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      getShell().execute("build");
   }

   @Test
   public void testCreateEndpointPrimitiveNonStandardGetterId() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass entity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User3.java"));
      entity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(entity);

      getShell().setCurrentResource(java.getJavaResource(entity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.User3Endpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/user3s", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               endpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      getShell().execute("build");
   }
   
   @Test
   public void testCreateEndpointWithAssociation() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass userEntity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User.java"));
      userEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(userEntity);
      JavaClass groupEntity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("Group.java"));
      groupEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(groupEntity);

      getShell().setCurrentResource(java.getJavaResource(groupEntity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity com.test.model.*");

      JavaResource userResource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass userEndpoint = (JavaClass) userResource.getJavaSource();

      assertEquals("/users", userEndpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               userEndpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertEquals("java.util.List", userEndpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> findUserByIdMethod = userEndpoint.getMethod("findById", Long.class);
      Type<JavaClass> returnTypeInspector = findUserByIdMethod.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector.getQualifiedName());

      JavaResource groupResource = java.getJavaResource(java.getBasePackage() + ".rest.GroupEndpoint");
      JavaClass groupEndpoint = (JavaClass) groupResource.getJavaSource();

      assertEquals("/groups", groupEndpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               groupEndpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertEquals("java.util.List", groupEndpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> findGroupByIdMethod = userEndpoint.getMethod("findById", Long.class);
      returnTypeInspector = findGroupByIdMethod.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector.getQualifiedName());

      getShell().execute("build");
   }
   
   @Test
   public void testCreateDTOBasedEndpoint() throws Exception
   {
      Project project = getProject();
      generateEntity(project, null, "User");

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity --strategy ROOT_AND_NESTED_DTO");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/users", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               endpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertNotNull(endpoint.getMethod("create","com.test.rest.dto.UserDTO"));
      assertEquals("java.util.List", endpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> method = endpoint.getMethod("findById", Long.class);
      Type<JavaClass> returnTypeInspector = method.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector.getQualifiedName());
      
      JavaResource dtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.UserDTO");
      JavaClass dto = (JavaClass) dtoResource.getJavaSource();
      assertNotNull(dto.getField("id"));
      assertEquals("Long", dto.getField("id").getType());
      assertNotNull(dto.getMethod("getId"));
      assertNotNull(dto.getMethod("setId","Long"));
      assertNotNull(dto.getField("version"));
      assertEquals("int", dto.getField("version").getType());
      assertNotNull(dto.getMethod("getVersion"));
      assertNotNull(dto.getMethod("setVersion","int"));

      getShell().execute("build");
   }
   
   @Test
   public void testCreateDTOBasedEndpointWithAssociation() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass userEntity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User.java"));
      userEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(userEntity);
      JavaClass groupEntity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("Group.java"));
      groupEntity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(groupEntity);

      getShell().setCurrentResource(java.getJavaResource(groupEntity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity com.test.model.* --strategy ROOT_AND_NESTED_DTO");

      JavaResource userResource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass userEndpoint = (JavaClass) userResource.getJavaSource();

      assertEquals("/users", userEndpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               userEndpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertNotNull(userEndpoint.getMethod("create","com.test.rest.dto.UserDTO"));
      assertEquals("java.util.List", userEndpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> findUserByIdMethod = userEndpoint.getMethod("findById", Long.class);
      Type<JavaClass> returnTypeInspector = findUserByIdMethod.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector.getQualifiedName());
      
      JavaResource userDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.UserDTO");
      JavaClass userDto = (JavaClass) userDtoResource.getJavaSource();
      assertNotNull(userDto.getField("objectId"));
      assertEquals("Long", userDto.getField("objectId").getType());
      assertNotNull(userDto.getMethod("getObjectId"));
      assertNotNull(userDto.getMethod("setObjectId","Long"));
      assertNotNull(userDto.getField("version"));
      assertEquals("int", userDto.getField("version").getType());
      assertNotNull(userDto.getMethod("getVersion"));
      assertNotNull(userDto.getMethod("setVersion","int"));

      JavaResource groupResource = java.getJavaResource(java.getBasePackage() + ".rest.GroupEndpoint");
      JavaClass groupEndpoint = (JavaClass) groupResource.getJavaSource();

      assertEquals("/groups", groupEndpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("forge-default",
               groupEndpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertNotNull(groupEndpoint.getMethod("create","com.test.rest.dto.GroupDTO"));
      assertEquals("java.util.List", groupEndpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> findGroupByIdMethod = userEndpoint.getMethod("findById", Long.class);
      returnTypeInspector = findGroupByIdMethod.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector.getQualifiedName());
      
      JavaResource groupDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.GroupDTO");
      JavaClass groupDto = (JavaClass) groupDtoResource.getJavaSource();
      assertNotNull(groupDto.getField("objectId"));
      assertEquals("Long", groupDto.getField("objectId").getType());
      assertNotNull(groupDto.getMethod("getObjectId"));
      assertNotNull(groupDto.getMethod("setObjectId","Long"));
      assertNotNull(groupDto.getField("version"));
      assertEquals("int", groupDto.getField("version").getType());
      assertNotNull(groupDto.getMethod("getVersion"));
      assertNotNull(groupDto.getMethod("setVersion","int"));
      assertNotNull(groupDto.getField("users"));
      assertEquals("Set", groupDto.getField("users").getType());
      assertNotNull(groupDto.getMethod("getUsers"));
      assertNotNull(groupDto.getMethod("setUsers","Set"));
      
      JavaResource nestedUserDtoResource = java.getJavaResource(java.getBasePackage() + ".rest.dto.NestedUserDTO");
      JavaClass nestedUserDto = (JavaClass) nestedUserDtoResource.getJavaSource();
      assertNotNull(nestedUserDto.getField("objectId"));
      assertEquals("Long", nestedUserDto.getField("objectId").getType());
      assertNotNull(nestedUserDto.getMethod("getObjectId"));
      assertNotNull(nestedUserDto.getMethod("setObjectId","Long"));
      assertNotNull(nestedUserDto.getField("version"));
      assertEquals("int", nestedUserDto.getField("version").getType());
      assertNotNull(nestedUserDto.getMethod("getVersion"));
      assertNotNull(nestedUserDto.getMethod("setVersion","int"));

      getShell().execute("build");
   }

   @Test
   public void testInstallWebIntoXML() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               JAX_RS_DEPENDENCY));

      ServletFacet web = project.getFacet(ServletFacet.class);
      Node servletName = ((WebAppDescriptorImpl) web.getConfig()).getRootNode().getSingle(
               "servlet-mapping/servlet-name=" + RestWebXmlFacetImpl.JAXRS_SERVLET);
      assertNotNull(servletName);
      assertEquals("/rest/*", servletName.getParent().getSingle("url-pattern").getText());
   }
   
   @Test
   public void testCreateEndpointWithMultiplePersistenceUnits() throws Exception
   {
      Project project = getProject();
      JavaClass entity = generateEntity(project, null, "User");
      assertFalse(entity.hasAnnotation(XmlRootElement.class));
      
      PersistenceFacet persistenceFacet = project.getFacet(PersistenceFacet.class);
      PersistenceDescriptor persistenceConfig = persistenceFacet.getConfig();
      PersistenceUnitDef defaultUnit = persistenceConfig.persistenceUnit(PersistencePlugin.DEFAULT_UNIT_NAME);
      persistenceConfig.persistenceUnit("rest-plugin-test").name("rest-plugin-test")
               .description("PU for REST plugin test")
               .provider(defaultUnit.getProvider())
               .transactionType(TransactionType.valueOf(defaultUnit.getTransactionType()))
               .jtaDataSource(defaultUnit.getJtaDataSource()).excludeUnlistedClasses();
      persistenceFacet.saveConfig(persistenceConfig);

      setupRest();

      queueInputLines("","2");
      getShell().execute("rest endpoint-from-entity");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/users", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("rest-plugin-test",
               endpoint.getField("em").getAnnotation(PersistenceContext.class).getStringValue("unitName"));
      assertEquals("java.util.List", endpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> method = endpoint.getMethod("findById", Long.class);
      Type<JavaClass> returnTypeInspector = method.getReturnTypeInspector();
      assertEquals("javax.ws.rs.core.Response", returnTypeInspector
                        .getQualifiedName());

      getShell().execute("build");
   }

   private void setupRest() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup rest");
   }
}
