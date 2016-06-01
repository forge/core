/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator.impl;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.forge.addon.javaee.jpa.JPAEntityUtil;
import org.jboss.forge.addon.javaee.rest.generation.RestGenerationConstants;
import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.javaee.rest.generator.ResourceGeneratorUtil;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateFactory;
import org.jboss.forge.addon.templates.freemarker.FreemarkerTemplate;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.Types;

/**
 * A JAX-RS resource generator that uses JPA entities directly in the created REST resources.
 */
public class EntityBasedResourceGenerator implements RestResourceGenerator
{
   @Inject
   TemplateFactory templateFactory;

   @Inject
   ResourceFactory resourceFactory;

   @Override
   public List<JavaClassSource> generateFrom(RestGenerationContext context) throws Exception
   {
      JavaClassSource entity = context.getEntity();
      Project project = context.getProject();
      if (!entity.hasAnnotation(XmlRootElement.class))
      {
         entity.addAnnotation(XmlRootElement.class);
         project.getFacet(JavaSourceFacet.class).saveJavaSource(entity);
      }
      String contentType = ResourceGeneratorUtil.getContentType(context.getContentType());
      String idType = JPAEntityUtil.resolveIdType(entity);
      String persistenceUnitName = context.getPersistenceUnitName();
      String idGetterName = JPAEntityUtil.resolveIdGetterName(entity);
      String entityTable = JPAEntityUtil.getEntityTable(entity);
      String selectExpression = JPAEntityUtil.getSelectExpression(entity, entityTable);
      String idClause = JPAEntityUtil.getIdClause(entity, entityTable);
      String orderClause = JPAEntityUtil.getOrderClause(entity,
               JPAEntityUtil.getJpqlEntityVariable(entityTable));
      String resourcePath = ResourceGeneratorUtil.getResourcePath(context);

      Map<Object, Object> map = new HashMap<>();
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
      map.put("idIsPrimitive", Types.isPrimitive(idType));

      Resource<URL> templateResource = resourceFactory.create(getClass().getResource("Endpoint.jv"));
      Template processor = templateFactory.create(templateResource, FreemarkerTemplate.class);
      String output = processor.process(map);
      JavaClassSource resource = Roaster.parse(JavaClassSource.class, output);
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
      return RestGenerationConstants.JPA_ENTITY;
   }
}
