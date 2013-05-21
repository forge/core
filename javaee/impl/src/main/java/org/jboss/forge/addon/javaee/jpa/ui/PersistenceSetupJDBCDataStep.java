/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JavaEEDefaultContainer;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputTypes;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class PersistenceSetupJDBCDataStep implements UIWizardStep
{

   @Inject
   @WithAttributes(label = "Database Type:", required = true)
   private UIInput<DatabaseType> dbType;

   @Inject
   @WithAttributes(label = "JDBC Driver:", required = true)
   UIInput<String> jdbcDriver;

   @Inject
   @WithAttributes(label = "Database URL:", required = true)
   UIInput<String> databaseURL;

   @Inject
   @WithAttributes(label = "Username:", required = true)
   UIInput<String> username;

   @Inject
   @WithAttributes(label = "Password:", required = true)
   UIInput<String> password;

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      // No next step
      return null;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(PersistenceSetupJDBCDataStep.class).name("JPA: JDBC Connection setup")
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
      PersistenceProvider pp = (PersistenceProvider) uiContext.getAttribute(PersistenceProvider.class);
      initDBType(pc, pp);
      password.getFacet(HintsFacet.class).setInputType(InputTypes.SECRET);
      builder.add(dbType).add(jdbcDriver).add(databaseURL).add(username).add(password);
   }

   private void initDBType(PersistenceContainer pc, PersistenceProvider pp)
   {
      if (pc instanceof JavaEEDefaultContainer)
      {
         dbType.setDefaultValue(((JavaEEDefaultContainer) pc).getDefaultDatabaseType());
      }
      else
      {
         dbType.setDefaultValue(DatabaseType.DEFAULT);
      }
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
      return Results.success("Persistence (JPA) is installed.");
   }

}
