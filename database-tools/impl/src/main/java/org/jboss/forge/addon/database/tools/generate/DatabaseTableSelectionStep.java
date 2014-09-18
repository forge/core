package org.jboss.forge.addon.database.tools.generate;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.jboss.forge.furnace.util.Lists;

/**
 * In this step, the user can choose the database tables
 */
public class DatabaseTableSelectionStep implements UIWizardStep
{
   private static final Logger logger = Logger.getLogger(DatabaseTableSelectionStep.class.getName());

   @Inject
   @WithAttributes(
            label = "Database Tables",
            description = "The database tables for which to generate entities. Use '*' to select all tables")
   private UISelectMany<String> databaseTables;

   @Inject
   private GenerateEntitiesCommandDescriptor descriptor;

   @Inject
   private HibernateToolsHelper helper;

   private JDBCMetaDataConfiguration jmdc;

   private List<String> tables;
   private Properties currentConnectionProperties;
   private Throwable exception;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Database Table Selection")
               .description("Select the database tables for which you want to generate entities");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      databaseTables.setValueChoices(new Callable<Iterable<String>>()
      {
         @Override
         public Iterable<String> call() throws Exception
         {
            if (!descriptor.getConnectionProperties().equals(currentConnectionProperties))
            {
               tables = new ArrayList<>();
               exception = null;
               currentConnectionProperties = descriptor.getConnectionProperties();
               jmdc = new JDBCMetaDataConfiguration();
               jmdc.setProperties(descriptor.getConnectionProperties());
               jmdc.setReverseEngineeringStrategy(createReverseEngineeringStrategy());
               try
               {
                  helper.buildMappings(descriptor.getUrls(), descriptor.getDriverClass(), jmdc);
                  Iterator<Table> iterator = jmdc.getTableMappings();
                  while (iterator.hasNext())
                  {
                     Table table = iterator.next();
                     tables.add(table.getName());
                  }
               }
               catch (Exception e)
               {
                  logger.log(Level.SEVERE, "Error while fetching database tables", e);
                  exception = e;
                  while (exception.getCause() != null)
                  {
                     exception = exception.getCause();
                  }
               }
            }
            return tables;
         }
      });
      builder.add(databaseTables);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Collection<String> entities = exportSelectedEntities();
      return Results.success(entities.size() + " entities were generated in " + descriptor.getTargetPackage());
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
      List<String> list = Lists.toList(databaseTables.getValue());
      if (list == null || list.isEmpty())
      {
         context.addValidationError(databaseTables, "At least one database table must be specified");
      }
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

   private Collection<String> exportSelectedEntities()
   {
      final Collection<String> selectedTableNames = Lists.toList(databaseTables.getValue());
      JavaSourceFacet java = descriptor.getSelectedProject().getFacet(JavaSourceFacet.class);
      POJOExporter pj = new POJOExporter(jmdc, java.getSourceDirectory().getUnderlyingResourceObject())
      {
         @Override
         @SuppressWarnings("rawtypes")
         protected void exportPOJO(Map additionalContext, POJOClass element)
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

   private ReverseEngineeringStrategy createReverseEngineeringStrategy()
   {
      ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();
      ReverseEngineeringSettings revengsettings =
               new ReverseEngineeringSettings(strategy)
                        .setDefaultPackageName(descriptor.getTargetPackage())
                        .setDetectManyToMany(true)
                        .setDetectOneToOne(true)
                        .setDetectOptimisticLock(true);
      strategy.setSettings(revengsettings);
      return strategy;
   }

}
