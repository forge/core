package org.jboss.forge.addon.database.tools.generate;

import java.io.File;

import org.jboss.forge.addon.database.tools.connections.AbstractConnectionProfileDetailsPage;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
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
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class ConnectionProfileDetailsStep extends AbstractConnectionProfileDetailsPage implements UIWizardStep
{
   private GenerateEntitiesCommandDescriptor descriptor;

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
      descriptor = (GenerateEntitiesCommandDescriptor) builder.getUIContext().getAttributeMap()
               .get(GenerateEntitiesCommandDescriptor.class);
      ConnectionProfileManagerProvider provider = SimpleContainer
               .getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
      ConnectionProfileManager manager = provider.getConnectionProfileManager();
      ConnectionProfile cp = manager.loadConnectionProfiles().get(descriptor.getConnectionProfileName());
      if (cp != null)
      {
         jdbcUrl.setValue(cp.getUrl());
         userName.setValue(cp.getUser());
         userPassword.setValue(cp.getPassword());
         hibernateDialect.setValue(HibernateDialect.fromClassName(cp.getDialect()));
         ResourceFactory factory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
                  .get();
         driverLocation.setValue((FileResource<?>) factory.create(new File(cp.getPath())));

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
         descriptor.setUrls(HibernateToolsHelper.getDriverUrls(driverLocation.getValue()));
      }
      if (driverClass.getValue() != null)
      {
         descriptor.setDriverClass(driverClass.getValue().getName());
      }
      descriptor.setConnectionProperties(createConnectionProperties());
   }

}
