/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.SchemaGenerationType;
import org.jboss.forge.addon.javaee.jpa.containers.JavaEEDefaultContainer;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class JPASetupConnectionStep extends AbstractJavaEECommand implements UIWizardStep
{
   @Inject
   @WithAttributes(shortName = 't', label = "Database Type", required = true)
   private UISelectOne<DatabaseType> dbType;

   @Inject
   @WithAttributes(shortName = 'd', label = "DataSource Name", required = true)
   private UIInput<String> dataSourceName;

   @Inject
   @WithAttributes(label = "JDBC Driver", required = true, type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> jdbcDriver;

   @Inject
   @WithAttributes(label = "Database URL", required = true)
   private UIInput<String> databaseURL;

   @Inject
   @WithAttributes(label = "Username", required = true)
   private UIInput<String> username;

   @Inject
   @WithAttributes(label = "Password", type = InputType.SECRET)
   private UIInput<String> password;

   @Inject
   @WithAttributes(label = "Persistence Unit Name", required = true)
   private UIInput<String> persistenceUnitName;

   @Inject
   @WithAttributes(label = "Overwrite Persistence Unit")
   private UIInput<Boolean> overwritePersistenceUnit;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Inject
   @WithAttributes(label = "Schema Generation Type", shortName = 's', required = true, type = InputType.RADIO)
   private UISelectOne<SchemaGenerationType> schemaGenerationType;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      // No next step
      return null;
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: Connection Settings")
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
      final UIContext uiContext = builder.getUIContext();
      PersistenceContainer pc = (PersistenceContainer) uiContext.getAttributeMap().get(PersistenceContainer.class);
      initDBType(uiContext);
      initDatasourceName(uiContext);
      initPersistenceUnitName(builder);
      builder.add(dbType);
      overwritePersistenceUnit.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            Project project = getSelectedProject(uiContext);
            if (persistenceUnitName.getValue() == null)
            {
               return false;
            }
            return isExistingPersistenceUnitName(project, persistenceUnitName.getValue());
         }
      });
      builder.add(overwritePersistenceUnit);
      if (pc.isDataSourceRequired())
      {
         builder.add(dataSourceName);
      }
      else
      {
         builder.add(jdbcDriver).add(databaseURL).add(username).add(password);
      }
      if (uiContext.getProvider().isGUI())
      {
         schemaGenerationType.setItemLabelConverter(new Converter<SchemaGenerationType, String>()
         {
            @Override
            public String convert(SchemaGenerationType source)
            {
               return source != null ? source.getLabel() : null;
            }
         });
      }
      schemaGenerationType.setDefaultValue(SchemaGenerationType.DROP_CREATE);
      schemaGenerationType.setDescription(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            return schemaGenerationType.getValue().getDescription();
         }
      });
      builder.add(schemaGenerationType);
   }

   private void initPersistenceUnitName(UIBuilder builder)
   {
      int i = 1;
      Project selectedProject = getSelectedProject(builder.getUIContext());
      String projectName = selectedProject == null ? "forge" : selectedProject.getFacet(MetadataFacet.class)
               .getProjectName();
      String unitName = projectName + PersistenceOperations.DEFAULT_UNIT_SUFFIX;
      while (isExistingPersistenceUnitName(selectedProject, unitName))
      {
         unitName = projectName + PersistenceOperations.DEFAULT_UNIT_SUFFIX + "-" + i++;
      }
      builder.add(persistenceUnitName.setDefaultValue(unitName));
   }

   private void initDatasourceName(final UIContext uiContext)
   {
      dataSourceName.setDefaultValue(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            PersistenceContainer pc = (PersistenceContainer) uiContext.getAttributeMap()
                     .get(PersistenceContainer.class);
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
            PersistenceContainer pc = (PersistenceContainer) uiContext.getAttributeMap()
                     .get(PersistenceContainer.class);
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
      dataSource.setSchemaGenerationType(schemaGenerationType.getValue());
      Map<Object, Object> attributeMap = context.getAttributeMap();
      dataSource.setProvider((PersistenceProvider) attributeMap.get(PersistenceProvider.class));
      dataSource.setContainer((PersistenceContainer) attributeMap.get(PersistenceContainer.class));
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
      // Validate Persistence Unit Name
      Project project = getSelectedProject(uiContext);
      if (isExistingPersistenceUnitName(project, persistenceUnitName.getValue())
               && !overwritePersistenceUnit.getValue().booleanValue())
      {
         boolean gui = uiContext.getProvider().isGUI();
         String info = gui ? " Check 'Overwrite Persistence Unit' if you want to overwrite it."
                  : " Use '--overwritePersistenceUnit' if you want to overwrite it.";
         validator.addValidationError(persistenceUnitName,
                  "A persistence-unit with the name [" + persistenceUnitName.getValue()
                           + "] already exists." + info);
      }
   }

   private boolean isExistingPersistenceUnitName(Project project, String unitName)
   {
      return persistenceOperations.getExistingPersistenceUnit(project, unitName) != null;
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JPADataSource dataSource = getDataSource(uiContext);
      Boolean configureMetadata = (Boolean) uiContext.getAttributeMap().get("ConfigureMetadata");
      String puName = persistenceUnitName.getValue();
      persistenceOperations.setup(puName, project, dataSource, configureMetadata);
      return Results.success("Persistence (JPA) is installed.");

   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

}
