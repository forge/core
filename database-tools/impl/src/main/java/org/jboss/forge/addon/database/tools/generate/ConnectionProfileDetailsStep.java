package org.jboss.forge.addon.database.tools.generate;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.addon.database.tools.connections.AbstractConnectionProfileDetailsPage;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.jpa.HibernateDialect;
import org.jboss.forge.addon.database.tools.util.HibernateToolsHelper;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ConnectionProfileDetailsStep extends AbstractConnectionProfileDetailsPage implements UIWizardStep
{
   @Inject
   private ConnectionProfileManager manager;

   @Inject
   private GenerateEntitiesCommandDescriptor descriptor;

   @Inject
   private ResourceFactory factory;

   @Inject
   private HibernateToolsHelper helper;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Connection Profile Details")
               .description("Edit the connection profile details");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      ConnectionProfile cp = manager.loadConnectionProfiles().get(descriptor.getConnectionProfileName());
      if (cp != null)
      {
         jdbcUrl.setValue(cp.getUrl());
         userName.setValue(cp.getUser());
         userPassword.setValue(cp.getPassword());
         hibernateDialect.setValue(HibernateDialect.fromClassName(cp.getDialect()));
         driverLocation.setValue((FileResource<?>) (FileResource<?>) factory.create(new File(cp.getPath())));

         for (Class<?> driver : driverClass.getValueChoices())
         {
            if (cp.getDriver().equals(driver.getName()))
               driverClass.setValue(driver);
         }
      }
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      return Results.success();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return Results.navigateTo(DatabaseTableSelectionStep.class);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      if (driverLocation.getValue() != null)
      {
         descriptor.setUrls(helper.getDriverUrls(driverLocation.getValue()));
      }
      if (driverClass.getValue() != null)
      {
    	  descriptor.setDriverClass(driverClass.getValue().getName());
      }
      descriptor.setConnectionProperties(createConnectionProperties());
   }

}
