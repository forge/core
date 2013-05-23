/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facets.PersistenceFacet;
import org.jboss.forge.addon.javaee.facets.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnitTransactionType;

public class PersistenceManager
{
   public static final String DEFAULT_UNIT_NAME = "forge-default";
   private static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   private FacetFactory facetFactory;

   @Inject
   public PersistenceManager(FacetFactory facetFactory)
   {
      super();
      this.facetFactory = facetFactory;
   }

   /**
    * Setups JPA in the project
    * 
    * @param project
    * @param dataSource
    * @param configureMetadata
    */
   public void setup(Project project, JPADataSource dataSource, boolean configureMetadata)
   {
      if (project != null)
      {
         if (!project.hasFacet(PersistenceFacet.class))
         {
            facetFactory.install(PersistenceFacet.class, project);
         }
         PersistenceFacet facet = project.getFacet(PersistenceFacet.class);
         PersistenceContainer container = dataSource.getContainer();
         PersistenceProvider provider = dataSource.getProvider();
         PersistenceDescriptor config = facet.getConfig();
         PersistenceUnit<PersistenceDescriptor> unit = config.createPersistenceUnit();
         unit.name(DEFAULT_UNIT_NAME).description(DEFAULT_UNIT_DESC);
         unit.transactionType(container.isJTASupported() ? PersistenceUnitTransactionType._JTA
                  : PersistenceUnitTransactionType._RESOURCE_LOCAL);
         unit.provider(provider.getProvider());

         container.setupConnection(unit, dataSource);
         provider.configure(unit, dataSource);
         facet.saveConfig(config);
      }
      if (configureMetadata)
      {
         facetFactory.install(PersistenceMetaModelFacet.class, project);
      }
   }

}
