/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.providers;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.MetaModelProvider;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaEEDefaultProvider implements PersistenceProvider
{

   @Override
   public String getName()
   {
      return "Java EE";
   }

   @Override
   public String getProvider()
   {
      return null;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds)
   {
      unit.excludeUnlistedClasses(Boolean.FALSE);
      // PropertiesCommon properties = unit.getOrCreateProperties();
      // properties.createProperty().name("javax.persistence.schema-generation.database.action").value("create");
      // properties.createProperty().name("javax.persistence.schema-generation.scripts.action").value("drop-and-create");
      return unit;
   }

   @Override
   public List<Dependency> listDependencies()
   {
      return Collections.emptyList();
   }

   @Override
   public MetaModelProvider getMetaModelProvider()
   {
      return new HibernateMetaModelProvider();
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
   }

}
