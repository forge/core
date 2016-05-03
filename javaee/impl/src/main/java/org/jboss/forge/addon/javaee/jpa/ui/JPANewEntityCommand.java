/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;
import javax.persistence.GenerationType;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(JavaSourceFacet.class)
public class JPANewEntityCommand extends AbstractJPACommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "ID Column Generation Strategy", defaultValue = "AUTO")
   private UISelectOne<GenerationType> idStrategy;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   @WithAttributes(label = "Table Name")
   private UIInput<String> tableName;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Inject
   private Configuration configuration;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("JPA: New Entity")
               .description("Create a new JPA Entity");
   }

   @Override
   protected String getType()
   {
      return "JPA Entity";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      idStrategy.setDefaultValue(GenerationType.AUTO);
      builder.add(idStrategy).add(tableName);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      GenerationType idStrategyChosen = idStrategy.getValue();
      if (idStrategyChosen == null)
      {
         idStrategyChosen = GenerationType.AUTO;
      }
      ConfigurationFacet facet = project.getFacet(ConfigurationFacet.class);
      Configuration config = facet.getConfiguration();
      String idPropertyName = config.getString(PersistenceOperations.ID_PROPERTY_NAME_CONFIGURATION_KEY,
               configuration.getString(PersistenceOperations.ID_PROPERTY_NAME_CONFIGURATION_KEY, "id"));
      String versionPropertyName = config.getString(PersistenceOperations.VERSION_PROPERTY_NAME_CONFIGURATION_KEY,
               configuration.getString(PersistenceOperations.VERSION_PROPERTY_NAME_CONFIGURATION_KEY, "version"));
      return persistenceOperations.newEntity(source, idStrategyChosen, tableName.getValue(), idPropertyName,
               versionPropertyName);
   }
}
