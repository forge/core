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
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertiesCommon;

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
   public PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds, Project project)
   {
      unit.excludeUnlistedClasses(Boolean.FALSE);
      PersistenceCommonDescriptor descriptor = (PersistenceCommonDescriptor) unit.up();
      if (new SingleVersion(descriptor.getVersion()).compareTo(new SingleVersion("2.1")) >= 0)
      {
         PropertiesCommon properties = unit.getOrCreateProperties();
         properties.createProperty().name("javax.persistence.schema-generation.database.action").value("drop-and-create");
         properties.createProperty().name("javax.persistence.schema-generation.scripts.action").value("drop-and-create");
         String createDdlFileName = project == null ? "create.ddl" : getProjectName(project) + "Create.ddl";
         properties.createProperty().name("javax.persistence.schema-generation.scripts.create-target").value(createDdlFileName);
         String dropDdlFileName = project == null ? "drop.ddl" : getProjectName(project) + "Drop.ddl";
         properties.createProperty().name("javax.persistence.schema-generation.scripts.drop-target").value(dropDdlFileName);
      }
      return unit;
   }

   private String getProjectName(Project project)
   {
      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      return metadata.getProjectName();
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
