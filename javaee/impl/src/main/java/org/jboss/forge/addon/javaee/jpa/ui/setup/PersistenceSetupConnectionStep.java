/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
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

public class PersistenceSetupConnectionStep implements UIWizardStep
{
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

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private PersistenceOperations persistenceOperations;

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

   private JPADataSource getDataSource(UIContext context)
   {
      JPADataSource dataSource = new JPADataSource();
      dataSource.setDatabase(dbType.getValue());
      dataSource.setJndiDataSource(dataSourceName.getValue());
      dataSource.setDatabaseURL(databaseURL.getValue());
      dataSource.setUsername(username.getValue());
      dataSource.setPassword(password.getValue());
      dataSource.setProvider((PersistenceProvider) context.getAttribute(PersistenceProvider.class));
      dataSource.setContainer((PersistenceContainer) context.getAttribute(PersistenceContainer.class));
      return dataSource;
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      UIContext uiContext = validator.getUIContext();
      JPADataSource ds = getDataSource(uiContext);
      try
      {
         ds.validate();
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
      JPADataSource dataSource = getDataSource(context);
      Boolean configureMetadata = (Boolean) context.getAttribute("ConfigureMetadata");
      persistenceOperations.setup(project, dataSource, configureMetadata);
      return Results.success("Persistence (JPA) is installed.");
   }

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

   public UISelectOne<DatabaseType> getDbType()
   {
      return dbType;
   }

   public UIInput<String> getDataSourceName()
   {
      return dataSourceName;
   }

   public UIInput<String> getJdbcDriver()
   {
      return jdbcDriver;
   }

   public UIInput<String> getDatabaseURL()
   {
      return databaseURL;
   }

   public UIInput<String> getUsername()
   {
      return username;
   }

   public UIInput<String> getPassword()
   {
      return password;
   }

}
