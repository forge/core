package org.jboss.forge.addon.database.tools.generate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.forge.addon.database.tools.util.HibernateToolsHelper;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class DatabaseTableSelectionStep implements UIWizardStep
{

   private static String NAME = "Database Table Selection";
   private static String DESCRIPTION = "Select the database tables for which you want to generate entities";

   @Inject
   @WithAttributes(
            label = "Database Tables",
            description = "The database tables for which to generate entities")
   private UISelectMany<String> databaseTables;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata
               .forCommand(getClass())
               .name(NAME)
               .description(DESCRIPTION);
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }
   
   @Inject
   private GenerateEntitiesCommandDescriptor descriptor;
   
   @Inject
   private HibernateToolsHelper helper;
   
   private JDBCMetaDataConfiguration jmdc;

   @SuppressWarnings("unchecked")
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      jmdc = new JDBCMetaDataConfiguration();
      jmdc.setProperties(descriptor.connectionProperties);
      jmdc.setReverseEngineeringStrategy(createReverseEngineeringStrategy());
      helper.buildMappings(descriptor.urls, descriptor.driverClass, jmdc);
      Iterator<Object> iterator = jmdc.getTableMappings();
      ArrayList<String> tables = new ArrayList<String>();
      while (iterator.hasNext()) {
         Object mapping = iterator.next();
         if (mapping instanceof Table) {
            Table table = (Table)mapping;
            tables.add(table.getName());
         }
      }
      databaseTables.setValueChoices(tables);
      databaseTables.setDefaultValue(tables);
      builder.add(databaseTables);
   }

   @Override
   public Result execute(UIExecutionContext context)
   { 
      exportSelectedEntities();
      return Results.success();
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }
   
   private boolean isSelected(Collection<String> selection, POJOClass element) {
      boolean result = false;
      if (element.isComponent()) {
         if (element instanceof ComponentPOJOClass) {
            ComponentPOJOClass cpc = (ComponentPOJOClass)element;
            Iterator<?> iterator = cpc.getAllPropertiesIterator();
            result = true;
            while (iterator.hasNext()) {
               Object object = iterator.next();
               if (object instanceof Property) {
                  Property property = (Property)object;
                  String tableName = property.getValue().getTable().getName();
                  if (!selection.contains(tableName)) {
                     result = false;
                     break;
                  }
               }
            }
         }
      } else {
         if (element instanceof EntityPOJOClass) {
            EntityPOJOClass epc = (EntityPOJOClass)element;
            Object object = epc.getDecoratedObject();
            if (object instanceof PersistentClass) {
               PersistentClass pc = (PersistentClass)object;
               Table table = pc.getTable();
               if (selection.contains(table.getName())) {
                  result = true;
               }               
            }
         }
      }
      return result;
   }
   
   private Collection<String> getSelectedTableNames() {
      ArrayList<String> result = new ArrayList<String>();
      Iterator<String> iterator = databaseTables.getValue().iterator();
      while (iterator.hasNext()) {
         result.add(iterator.next());
      }
      return result;
   }
   
   private void exportSelectedEntities()
   {     
      final Collection<String> selectedTableNames = getSelectedTableNames();     
      JavaSourceFacet java = descriptor.selectedProject.getFacet(JavaSourceFacet.class);
      POJOExporter pj = new POJOExporter(jmdc, java.getSourceDirectory()
               .getUnderlyingResourceObject()) {
         @Override
         @SuppressWarnings("rawtypes")
         protected void exportPOJO(Map additionalContext, POJOClass element) {
            if (isSelected(selectedTableNames, element)) {
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
   }

   private ReverseEngineeringStrategy createReverseEngineeringStrategy()
   {
      ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();
      ReverseEngineeringSettings revengsettings =
               new ReverseEngineeringSettings(strategy)
                        .setDefaultPackageName(descriptor.targetPackage)
                        .setDetectManyToMany(true)
                        .setDetectOneToOne(true)
                        .setDetectOptimisticLock(true);
      strategy.setSettings(revengsettings);
      return strategy;
   }

}
