/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.generate;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jboss.forge.addon.database.tools.util.JDBCUtils;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Lists;

/**
 * In this step, the user can choose the database tables
 */
public class DatabaseTableSelectionStep extends AbstractProjectCommand implements UIWizardStep
{
   private static final String LAST_USED_CONNECTION_PROPERTIES = "LastUsedConnectionProperties";

   private UISelectOne<String> databaseCatalog;
   private UISelectOne<String> databaseSchema;
   private UISelectMany<String> databaseTables;

   private volatile Set<String> catalogValueChoices;
   private volatile Set<String> schemaValueChoices;
   private volatile Set<String> tableValueChoices;

   private Throwable exception;

   private final GenerateEntitiesCommandDescriptor descriptor;

   private static final Logger logger = Logger.getLogger(DatabaseTableSelectionStep.class.getName());

   public DatabaseTableSelectionStep(GenerateEntitiesCommandDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Database Table Selection")
               .description("Select the database tables for which you want to generate entities");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      if (databaseCatalog == null)
      {
         InputComponentFactory factory = builder.getInputComponentFactory();

         databaseCatalog = factory.createSelectOne("databaseCatalog", String.class)
                  .setLabel("Database Catalog")
                  .setDescription("The database catalog for which to generate entities.")
                  .setDefaultValue(() -> {
                     Iterator<String> it = databaseCatalog.getValueChoices().iterator();
                     return it.hasNext() ? it.next() : null;
                  })
                  .setValueChoices(() -> catalogValueChoices);

         databaseCatalog.addValueChangeListener((event) -> updateValueChoices(context, event));

         databaseSchema = factory.createSelectOne("databaseSchema", String.class)
                  .setLabel("Database Schema")
                  .setDescription("The database schema for which to generate entities.")
                  .setDefaultValue(() -> {
                     Iterator<String> it = databaseSchema.getValueChoices().iterator();
                     return it.hasNext() ? it.next() : null;
                  })
                  .setValueChoices(() -> schemaValueChoices);

         databaseSchema.addValueChangeListener((event) -> updateValueChoices(context, event));

         databaseTables = factory.createSelectMany("databaseTables", String.class)
                  .setLabel("Database Tables")
                  .setDescription("The database tables for which to generate entities. Use '*' to select all tables")
                  .setValueChoices(() -> tableValueChoices);
      }
      Database database = updateValueChoices(context, null);
      if (database != null)
      {
         if (database.isCatalogSet())
         {
            databaseCatalog.setValue(database.getCatalog());
         }
         if (database.isSchemaSet())
         {
            databaseSchema.setValue(database.getSchema());
         }
      }
      builder.add(databaseCatalog).add(databaseSchema).add(databaseTables);
   }

   private boolean connectionInfoHasChanged(UIContext context)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      Properties currentConnectionProperties = (Properties) attributeMap.get(LAST_USED_CONNECTION_PROPERTIES);
      return !Objects.equals(descriptor.getConnectionProperties(), currentConnectionProperties);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      UIContext uiContext = context.getUIContext();
      updateValueChoices(uiContext, null);
      if (exception != null)
      {
         if (exception instanceof UnknownHostException)
         {
            context.addValidationError(databaseTables, "Unknown host: " + exception.getMessage());
         }
         else
         {
            String message = exception.getMessage();
            if (message == null)
            {
               message = String.format("%s during validation. Check logs for more information",
                        exception.getClass().getName());
            }
            context.addValidationError(databaseTables, message);
         }
      }
      else
      {
         List<String> list = Lists.toList(databaseTables.getValue());
         if (list == null || list.isEmpty())
         {
            context.addValidationError(databaseTables, "At least one database table must be specified");
         }
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaSourceFacet java = getSelectedProject(context).getFacet(JavaSourceFacet.class);
      String catalog = databaseCatalog.getValue();
      String schema = databaseSchema.getValue();
      Collection<String> tables = Lists.toList(databaseTables.getValue());
      EntityGenerator entityGenerator = new EntityGenerator();
      Collection<String> entities = entityGenerator
               .exportSelectedEntities(java.getSourceDirectory().getUnderlyingResourceObject(),
                        descriptor, catalog, schema, tables);
      return Results.success(entities.size() + " entities were generated in " + descriptor.getTargetPackage());
   }

   private synchronized Database getDatabase(UIContext context)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      Database database = (Database) attributeMap.get(Database.class.getName());
      if (database == null || connectionInfoHasChanged(context))
      {
         try
         {
            database = JDBCUtils.getDatabaseInfo(descriptor);
            exception = null;
            attributeMap.put(Database.class.getName(), database);
            attributeMap.put(LAST_USED_CONNECTION_PROPERTIES, descriptor.getConnectionProperties());
         }
         catch (Exception e)
         {
            attributeMap.remove(Database.class.getName(), database);
            attributeMap.remove(LAST_USED_CONNECTION_PROPERTIES, descriptor.getConnectionProperties());
            logger.log(Level.SEVERE, "Error while fetching the DB info", exception);
            exception = e;
         }
      }
      return database;
   }

   private Database updateValueChoices(UIContext context, ValueChangeEvent event)
   {
      Database database = getDatabase(context);
      List<DatabaseTable> tables = new ArrayList<>();
      if (database != null)
      {
         tables.addAll(database.getTables());
      }
      // Update Catalogs
      catalogValueChoices = tables
               .stream()
               .map(DatabaseTable::getCatalog)
               .filter(Objects::nonNull)
               .collect(Collectors.toCollection(TreeSet::new));
      final String catalog = (event != null && event.getSource() == databaseCatalog) ? (String) event.getNewValue()
               : databaseCatalog.getValue();
      // Update schemas
      schemaValueChoices = tables
               .stream()
               .filter(item -> Objects.equals(item.getCatalog(), catalog))
               .map(DatabaseTable::getSchema)
               .filter(Objects::nonNull)
               .collect(Collectors.toCollection(TreeSet::new));
      final String schema = (event != null && event.getSource() == databaseSchema) ? (String) event.getNewValue()
               : databaseSchema.getValue();
      // Update tables
      tableValueChoices = tables
               .stream()
               .filter(item -> Objects.equals(item.getCatalog(), catalog))
               .filter(item -> Objects.equals(item.getSchema(), schema))
               .map(DatabaseTable::getName)
               .filter(Objects::nonNull)
               .collect(Collectors.toCollection(TreeSet::new));
      return database;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}