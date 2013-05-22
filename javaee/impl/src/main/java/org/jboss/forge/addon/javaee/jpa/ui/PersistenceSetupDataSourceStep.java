/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JavaEEDefaultContainer;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class PersistenceSetupDataSourceStep implements UIWizardStep
{

   @Inject
   @WithAttributes(label = "Database Type:", required = true)
   private UISelectOne<DatabaseType> dbType;

   @Inject
   @WithAttributes(label = "DataSource Name:", required = true)
   private UIInput<String> dataSourceName;

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      // No next step
      return null;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(PersistenceSetupDataSourceStep.class).name("JPA: Datasource setup")
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
      initDBType(uiContext);
      initDatasourceName(uiContext);
      builder.add(dbType).add(dataSourceName);
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
      return Results.success("Persistence (JPA) is installed.");
   }

}
