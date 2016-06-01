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
import java.util.stream.Collectors;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.forge.addon.database.tools.util.HibernateToolsHelper;
import org.jboss.forge.addon.database.tools.util.JDBCUtils;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
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
import org.jboss.forge.furnace.util.Lists;

/**
 * In this step, the user can choose the database tables
 */
public class DatabaseTableSelectionStep implements UIWizardStep
{
   private UISelectOne<String> databaseCatalog;
   private UISelectOne<String> databaseSchema;
   private UISelectMany<String> databaseTables;

   private Set<String> catalogValueChoices;
   private Set<String> schemaValueChoices;
   private Set<String> tableValueChoices;

   private Throwable exception;

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
      updateValueChoices(context, null);
      builder.add(databaseCatalog).add(databaseSchema).add(databaseTables);
   }

   private boolean connectionInfoHasChanged(UIContext context)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      GenerateEntitiesCommandDescriptor descriptor = getDescriptor(context);
      Properties currentConnectionProperties = (Properties) attributeMap.get("DatabaseTableProperties");
      return !Objects.equals(descriptor.getConnectionProperties(), currentConnectionProperties);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      if (exception != null)
      {
         if (exception instanceof UnknownHostException)
         {
            context.addValidationError(databaseTables, "Unknown host: " + exception.getMessage());
         }
         else
         {
            context.addValidationError(databaseTables, exception.getMessage());
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
      GenerateEntitiesCommandDescriptor descriptor = getDescriptor(context.getUIContext());
      Collection<String> entities = exportSelectedEntities(descriptor);
      return Results.success(entities.size() + " entities were generated in " + descriptor.getTargetPackage());
   }

   private Collection<String> exportSelectedEntities(GenerateEntitiesCommandDescriptor descriptor) throws Exception
   {
      String catalog = databaseCatalog.getValue();
      String schema = databaseSchema.getValue();
      Collection<String> selectedTableNames = Lists.toList(databaseTables.getValue());
      JavaSourceFacet java = descriptor.getSelectedProject().getFacet(JavaSourceFacet.class);
      JDBCMetaDataConfiguration jmdc = new JDBCMetaDataConfiguration();
      jmdc.setProperties(descriptor.getConnectionProperties());
      jmdc.setReverseEngineeringStrategy(
               createReverseEngineeringStrategy(descriptor, catalog, schema, selectedTableNames));
      HibernateToolsHelper.buildMappings(descriptor.getUrls(), descriptor.getDriverClass(), jmdc);
      POJOExporter pj = new POJOExporter(jmdc, java.getSourceDirectory().getUnderlyingResourceObject())
      {
         @Override
         protected void exportPOJO(Map<String, Object> additionalContext, POJOClass element)
         {
            if (isSelected(selectedTableNames, element))
            {
               super.exportPOJO(additionalContext, element);
            }
         }
      };
      Properties pojoProperties = new Properties();
      pojoProperties.setProperty("jdk5", "true");
      pojoProperties.setProperty("ejb3", "true");
      pj.setProperties(pojoProperties);
      pj.setArtifactCollector(new ArtifactCollector());
      pj.start();
      return selectedTableNames;
   }

   private ReverseEngineeringStrategy createReverseEngineeringStrategy(GenerateEntitiesCommandDescriptor descriptor,
            String catalog, String schema, Collection<String> selectedTableNames)
   {
      ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy()
      {
         @Override
         public List<org.hibernate.cfg.reveng.SchemaSelection> getSchemaSelections()
         {
            return selectedTableNames
                     .stream()
                     .map((table) -> new SchemaSelection(catalog, schema, table))
                     .collect(Collectors.toList());
         }
      };

      ReverseEngineeringSettings revengsettings = new ReverseEngineeringSettings(strategy)
               .setDefaultPackageName(descriptor.getTargetPackage())
               .setDetectManyToMany(true)
               .setDetectOneToOne(true)
               .setDetectOptimisticLock(true);
      strategy.setSettings(revengsettings);
      return strategy;
   }

   private boolean isSelected(Collection<String> selection, POJOClass element)
   {
      boolean result = false;
      if (element.isComponent())
      {
         if (element instanceof ComponentPOJOClass)
         {
            ComponentPOJOClass cpc = (ComponentPOJOClass) element;
            Iterator<?> iterator = cpc.getAllPropertiesIterator();
            result = true;
            while (iterator.hasNext())
            {
               Object object = iterator.next();
               if (object instanceof Property)
               {
                  Property property = (Property) object;
                  String tableName = property.getValue().getTable().getName();
                  if (!selection.contains(tableName))
                  {
                     result = false;
                     break;
                  }
               }
            }
         }
      }
      else
      {
         if (element instanceof EntityPOJOClass)
         {
            EntityPOJOClass epc = (EntityPOJOClass) element;
            Object object = epc.getDecoratedObject();
            if (object instanceof PersistentClass)
            {
               PersistentClass pc = (PersistentClass) object;
               Table table = pc.getTable();
               if (selection.contains(table.getName()))
               {
                  result = true;
               }
            }
         }
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   private synchronized List<DatabaseTable> getTables(UIContext context)
   {
      List<DatabaseTable> allTables = new ArrayList<>();
      Map<Object, Object> attributeMap = context.getAttributeMap();
      List<DatabaseTable> tables = (List<DatabaseTable>) attributeMap
               .get(DatabaseTable.class.getName());
      if (tables == null || connectionInfoHasChanged(context))
      {
         GenerateEntitiesCommandDescriptor descriptor = getDescriptor(context);
         try
         {
            tables = JDBCUtils.getTables(descriptor);
         }
         catch (Exception e)
         {
            exception = e;
         }
         attributeMap.put(DatabaseTable.class.getName(), tables);
         attributeMap.put("DatabaseTableProperties", descriptor.getConnectionProperties());
      }
      if (tables != null)
      {
         allTables.addAll(tables);
      }
      return allTables;
   }

   private GenerateEntitiesCommandDescriptor getDescriptor(UIContext context)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      return (GenerateEntitiesCommandDescriptor) attributeMap.get(GenerateEntitiesCommandDescriptor.class);
   }

   private void updateValueChoices(UIContext context, ValueChangeEvent event)
   {
      List<DatabaseTable> tables = getTables(context);
      // Update Catalogs
      catalogValueChoices = tables
               .stream()
               .map((item) -> item.getCatalog())
               .filter(Objects::nonNull)
               .collect(Collectors.toCollection(TreeSet::new));
      final String catalog = (event != null && event.getSource() == databaseCatalog) ? (String) event.getNewValue()
               : databaseCatalog.getValue();
      // Update schemas
      schemaValueChoices = tables
               .stream()
               .filter((item) -> Objects.equals(item.getCatalog(), catalog))
               .map((item) -> item.getSchema())
               .filter(Objects::nonNull)
               .collect(Collectors.toCollection(TreeSet::new));
      final String schema = (event != null && event.getSource() == databaseSchema) ? (String) event.getNewValue()
               : databaseSchema.getValue();
      // Update tables
      tableValueChoices = tables
               .stream()
               .filter(item -> Objects.equals(item.getCatalog(), catalog))
               .filter(item -> Objects.equals(item.getSchema(), schema))
               .map((item) -> item.getName())
               .filter(Objects::nonNull)
               .collect(Collectors.toCollection(TreeSet::new));
   }
}
