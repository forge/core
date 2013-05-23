/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facets.PersistenceFacet;
import org.jboss.forge.addon.javaee.facets.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JavaEEDefaultContainer;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputTypes;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnitTransactionType;

public class PersistenceSetupConnectionStep implements UIWizardStep
{
   public static final String DEFAULT_UNIT_NAME = "forge-default";
   private static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(label = "Database Type:", required = true)
   private UISelectOne<DatabaseType> dbType;

   @Inject
   @WithAttributes(label = "DataSource Name:", required = true)
   private UIInput<String> dataSourceName;

   @Inject
   @WithAttributes(label = "JDBC Driver:", required = true)
   private UIInput<String> jdbcDriver;

   @Inject
   @WithAttributes(label = "Database URL:", required = true)
   private UIInput<String> databaseURL;

   @Inject
   @WithAttributes(label = "Username:", required = true)
   private UIInput<String> username;

   @Inject
   @WithAttributes(label = "Password:", required = true)
   private UIInput<String> password;

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      // No next step
      return null;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(PersistenceSetupConnectionStep.class).name("JPA: Connection Settings")
               .description("Configure your connection settings");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext uiContext = builder.getUIContext();
      PersistenceContainer pc = (PersistenceContainer) uiContext.getAttribute(PersistenceContainer.class);
      initDBType(uiContext);
      initDatasourceName(uiContext);
      builder.add(dbType);
      if (pc.isJTASupported())
      {
         builder.add(dataSourceName);
      }
      else
      {
         password.getFacet(HintsFacet.class).setInputType(InputTypes.SECRET);
         builder.add(jdbcDriver).add(databaseURL).add(username).add(password);
      }
   }

   private void initDatasourceName(final UIContext uiContext)
   {
      dataSourceName.setDefaultValue(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            PersistenceContainer pc = (PersistenceContainer) uiContext.getAttribute(PersistenceContainer.class);
            if (pc instanceof JavaEEDefaultContainer)
            {
               return ((JavaEEDefaultContainer) pc).getDefaultDataSource();
            }
            return null;
         }
      });
   }

   private void initDBType(final UIContext uiContext)
   {
      dbType.setDefaultValue(new Callable<DatabaseType>()
      {
         @Override
         public DatabaseType call() throws Exception
         {
            DatabaseType type = DatabaseType.DEFAULT;
            PersistenceContainer pc = (PersistenceContainer) uiContext.getAttribute(PersistenceContainer.class);
            if (pc instanceof JavaEEDefaultContainer)
            {
               type = ((JavaEEDefaultContainer) pc).getDefaultDatabaseType();
            }
            return type;
         }
      });
   }

   private JPADataSource getDataSource()
   {
      JPADataSource dataSource = new JPADataSource();
      dataSource.setDatabase(dbType.getValue());
      dataSource.setDatabaseURL(databaseURL.getValue());
      dataSource.setUsername(username.getValue());
      dataSource.setPassword(password.getValue());
      return dataSource;
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      UIContext uiContext = validator.getUIContext();
      PersistenceContainer pc = (PersistenceContainer) uiContext.getAttribute(PersistenceContainer.class);
      PersistenceProvider pp = (PersistenceProvider) uiContext.getAttribute(PersistenceProvider.class);
      JPADataSource ds = getDataSource();
      ds.setContainer(pc);
      ds.setProvider(pp);
      try
      {
         pc.validate(ds);
         pp.validate(ds);
      }
      catch (Exception e)
      {
         validator.addValidationError(null, e.getMessage());
      }
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(PersistenceFacet.class))
         {
            facetFactory.install(PersistenceFacet.class, project);
         }
         PersistenceContainer container = (PersistenceContainer) context.getAttribute(PersistenceContainer.class);
         PersistenceProvider provider = (PersistenceProvider) context.getAttribute(PersistenceProvider.class);
         PersistenceFacet facet = project.getFacet(PersistenceFacet.class);
         JPADataSource dataSource = getDataSource();

         // Setup JPA
         PersistenceDescriptor config = facet.getConfig();
         PersistenceUnit<PersistenceDescriptor> unit = config.createPersistenceUnit();
         unit.name(DEFAULT_UNIT_NAME).description(DEFAULT_UNIT_DESC);
         unit.transactionType(container.isJTASupported() ? PersistenceUnitTransactionType._JTA
                  : PersistenceUnitTransactionType._RESOURCE_LOCAL);
         unit.provider(provider.getProvider());

         container.setupConnection(unit, dataSource);
         provider.configure(unit, dataSource);
         facet.saveConfig(config);
         if ((Boolean) context.getAttribute("ConfigureMetadata"))
         {
            facetFactory.install(PersistenceMetaModelFacet.class, project);
         }
      }
      return Results.success("Persistence (JPA) is installed.");
   }

   // Helper methods

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      UISelection<Resource<?>> initialSelection = context.getInitialSelection();
      Resource<?> resource = initialSelection.get();
      Project project = null;
      if (resource instanceof DirectoryResource)
      {
         project = projectFactory.findProject((DirectoryResource) resource);
      }
      return project;
   }

}
