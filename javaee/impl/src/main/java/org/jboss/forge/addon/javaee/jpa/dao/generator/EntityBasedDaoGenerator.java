/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.dao.generator;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.jpa.JPAEntityUtil;
import org.jboss.forge.addon.javaee.jpa.dao.DaoGenerationContext;
import org.jboss.forge.addon.javaee.jpa.dao.DaoResourceGenerator;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateFactory;
import org.jboss.forge.addon.templates.freemarker.FreemarkerTemplate;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Generates Dao from JPA Entity.
 * 
 * @author <a href="salem.elrahal@gmail.com">Salem Elrahal</a>
 */
public class EntityBasedDaoGenerator implements DaoResourceGenerator
{

   @Inject
   TemplateFactory templateFactory;

   @Inject
   ResourceFactory resourceFactory;

   @Override
   public List<JavaClassSource> generateFrom(DaoGenerationContext context)
            throws Exception
   {
      JavaClassSource entity = context.getEntity();
      String idType = JPAEntityUtil.resolveIdType(entity);
      String persistenceUnitName = context.getPersistenceUnitName();
      String entityTable = JPAEntityUtil.getEntityTable(entity);
      String selectExpression = JPAEntityUtil.getSelectExpression(entity, entityTable);
      String orderClause = JPAEntityUtil.getOrderClause(entity,
               JPAEntityUtil.getJpqlEntityVariable(entityTable));

      Map<Object, Object> map = new HashMap<>();
      map.put("entity", entity);
      map.put("idType", idType);
      map.put("persistenceUnitName", persistenceUnitName);
      map.put("selectExpression", selectExpression);
      map.put("orderClause", orderClause);

      Resource<URL> templateResource = resourceFactory.create(getClass().getResource("Dao.jv"));
      Template processor = templateFactory.create(templateResource, FreemarkerTemplate.class);
      String output = processor.process(map);
      JavaClassSource resource = Roaster.parse(JavaClassSource.class, output);
      resource.addImport(entity.getQualifiedName());
      resource.setPackage(context.getTargetPackageName());
      return Arrays.asList(resource);
   }

   @Override
   public String getName()
   {
      return "JPA_ENTITY";
   }

   @Override
   public String getDescription()
   {
      return "Provide CRUD operations for JPA entities";
   }

}
