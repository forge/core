package org.jboss.forge.addon.database.tools.generate;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class EntityGenerator
{

   public Collection<String> exportSelectedEntities(File sourceDirectory, GenerateEntitiesCommandDescriptor descriptor,
            String catalog,
            String schema, Collection<String> tables) throws Exception
   {
      JDBCMetaDataConfiguration jmdc = new JDBCMetaDataConfiguration();
      jmdc.setProperties(descriptor.getConnectionProperties());
      jmdc.setReverseEngineeringStrategy(
               createReverseEngineeringStrategy(descriptor, catalog, schema, tables));
      HibernateToolsHelper.buildMappings(descriptor.getUrls(), descriptor.getDriverClass(), jmdc);
      POJOExporter pj = new POJOExporter(jmdc, sourceDirectory)
      {
         @Override
         protected void exportPOJO(Map<String, Object> additionalContext, POJOClass element)
         {
            if (isSelected(tables, element))
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
      return tables;
   }

   private ReverseEngineeringStrategy createReverseEngineeringStrategy(GenerateEntitiesCommandDescriptor descriptor,
            String catalog, String schema, Collection<String> selectedTableNames)
   {
      ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy()
      {
         @Override
         public List<SchemaSelection> getSchemaSelections()
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
      else if (element instanceof EntityPOJOClass)
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
      return result;
   }
}
