/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

/**
 * A JAX-RS resource generator that uses JPA entities directly in the created REST resources.
 */
public class EntityBasedResourceGenerator implements RestResourceGenerator
{
   @Inject
   FreemarkerTemplateProcessor processor;

   @Override
   public List<JavaClass> generateFrom(RestGenerationContext context) throws Exception
   {
      JavaClass entity = context.getEntity();
      Project project = context.getProject();
      String contentType = context.getContentType();
      if (!entity.hasAnnotation(XmlRootElement.class))
      {
         entity.addAnnotation(XmlRootElement.class);
         project.getFacet(JavaSourceFacet.class).saveJavaSource(entity);
      }
      String idType = ResourceGeneratorUtil.resolveIdType(entity);
      String persistenceUnitName = context.getPersistenceUnitName();
      String idGetterName = ResourceGeneratorUtil.resolveIdGetterName(entity);
      String entityTable = ResourceGeneratorUtil.getEntityTable(entity);
      String selectExpression = ResourceGeneratorUtil.getSelectExpression(entity, entityTable);
      String idClause = ResourceGeneratorUtil.getIdClause(entity, entityTable);
      String orderClause = ResourceGeneratorUtil.getOrderClause(entity,
               ResourceGeneratorUtil.getJpqlEntityVariable(entityTable));
      String resourcePath = ResourceGeneratorUtil.getResourcePath(context);

      Map<Object, Object> map = new HashMap<Object, Object>();
      map.put("entity", entity);
      map.put("idType", idType);
      map.put("getIdStatement", idGetterName);
      map.put("contentType", contentType);
      map.put("persistenceUnitName", persistenceUnitName);
      map.put("entityTable", entityTable);
      map.put("selectExpression", selectExpression);
      map.put("idClause", idClause);
      map.put("orderClause", orderClause);
      map.put("resourcePath", resourcePath);

      String output = processor.processTemplate(map, "org/jboss/forge/rest/Endpoint.jv");
      JavaClass resource = JavaParser.parse(JavaClass.class, output);
      resource.addImport(entity.getQualifiedName());
      resource.setPackage(context.getTargetPackageName());
      return Arrays.asList(resource);
   }

   @Override
   public String getDescription()
   {
      return "Expose JPA entities directly in the REST resources";
   }

   @Override
   public String getName()
   {
      return "JPA_ENTITY";
   }
}
