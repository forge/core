/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.generate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Entity;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.database.tools.util.HibernateToolsHelper;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.JavaSource;

@FacetConstraint(JPAFacet.class)
public class GenerateEntitiesCommand extends AbstractProjectCommand implements
         UIWizard
{
   private static String[] COMMAND_CATEGORY = { "Java EE", "JPA" };
   private static String COMMAND_NAME = "JPA: Generate Entities From Tables";
   private static String COMMAND_DESCRIPTION = "Command to generate Java EE entities from database tables.";

   private GenerateEntitiesCommandDescriptor descriptor = new GenerateEntitiesCommandDescriptor();

   private ConnectionProfileDetailsStep connectionProfileDetailsStep = new ConnectionProfileDetailsStep(descriptor);
   private DatabaseTableSelectionStep dbTableSelectionStep = new DatabaseTableSelectionStep(descriptor);

   private UIInput<String> targetPackage;
   private UISelectOne<String> connectionProfile;
   private UIInput<String> connectionProfilePassword;

   private Map<String, ConnectionProfile> profiles;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      targetPackage = factory.createInput("targetPackage", String.class).setLabel("Target package")
               .setDescription("The name of the target package in which to generate the entities").setRequired(true);
      targetPackage.getFacet(HintsFacet.class).setInputType(InputType.JAVA_PACKAGE_PICKER);
      targetPackage.setValueConverter(new PackageRootConverter(getProjectFactory(), builder));

      connectionProfile = factory.createSelectOne("connectionProfile", String.class).setLabel("Connection Profile")
               .setDescription("Select the database connection profile you want to use");
      connectionProfilePassword = factory.createInput("connectionProfilePassword", String.class)
               .setLabel("Connection Profile Password")
               .setDescription("Enter the database connection profile password");
      connectionProfilePassword.getFacet(HintsFacet.class).setInputType(InputType.SECRET);

      Project project = getSelectedProject(builder.getUIContext());
      targetPackage.setDefaultValue(calculateModelPackage(project));
      ConnectionProfileManagerProvider managerProvider = SimpleContainer
               .getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
      ConnectionProfileManager manager = managerProvider.getConnectionProfileManager();
      profiles = manager.loadConnectionProfiles();
      ArrayList<String> profileNames = new ArrayList<>();
      profileNames.add("");
      profileNames.addAll(profiles.keySet());
      connectionProfile.setValueChoices(profileNames);
      connectionProfile.setValue("");
      // Enable password input only if profile does not store saved passwords
      connectionProfilePassword.setEnabled(() -> {
         String connectionProfileName = connectionProfile.getValue();
         if (Strings.isNullOrEmpty(connectionProfileName))
            return false;
         return !profiles.get(connectionProfileName).isSavePassword();
      });
      builder.add(targetPackage).add(connectionProfile).add(connectionProfilePassword);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      return Results.success();
   }

   protected String getParameters()
   {
      return targetPackage.getValue();
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public NavigationResult next(UINavigationContext context)
   {
      descriptor.setTargetPackage(targetPackage.getValue());
      descriptor.setConnectionProfileName(connectionProfile.getValue());
      NavigationResultBuilder navigationResultBuilder = NavigationResultBuilder.create();
      if (Strings.isNullOrEmpty(descriptor.getConnectionProfileName()))
      {
         descriptor.setDriverClass(null);
         descriptor.setUrls(null);
         descriptor.setConnectionProperties(null);
         navigationResultBuilder.add(connectionProfileDetailsStep);
      }
      else
      {
         ConnectionProfile profile = profiles.get(descriptor.getConnectionProfileName());
         if (profile.getPath() != null)
         {
            descriptor.setUrls(HibernateToolsHelper.getDriverUrls(createResource(profile.getPath())));
         }
         descriptor.setDriverClass(profile.getDriver());
         descriptor.setConnectionProperties(createConnectionProperties(profile));
      }
      navigationResultBuilder.add(dbTableSelectionStep);
      return navigationResultBuilder.build();
   }

   private Properties createConnectionProperties(ConnectionProfile profile)
   {
      Properties result = new Properties();
      result.setProperty("hibernate.connection.driver_class",
               profile.getDriver() == null ? "" : profile.getDriver());
      result.setProperty("hibernate.connection.username",
               profile.getUser() == null ? "" : profile.getUser());
      result.setProperty("hibernate.dialect",
               profile.getDialect() == null ? "" : profile.getDialect());
      String profilePassword;
      // If password is not saved, user must provide it
      if (profile.isSavePassword())
      {
         profilePassword = profile.getPassword();
      }
      else
      {
         profilePassword = connectionProfilePassword.getValue();
      }
      if (profilePassword == null)
         profilePassword = "";

      result.setProperty("hibernate.connection.password", profilePassword);
      result.setProperty("hibernate.connection.url",
               profile.getUrl() == null ? "" : profile.getUrl());
      return result;
   }

   /**
    * @param project
    * @return
    */
   private String calculateModelPackage(Project project)
   {
      final String[] value = new String[1];
      project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
      {
         @Override
         public void visit(VisitContext context, JavaResource javaResource)
         {
            try
            {
               JavaSource<?> javaSource = javaResource.getJavaType();
               if (javaSource.hasAnnotation(Entity.class))
               {
                  value[0] = javaSource.getPackage();
               }
            }
            catch (FileNotFoundException ignore)
            {
            }
         }
      });
      if (value[0] == null)
      {
         value[0] = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      }
      return value[0];
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

   @SuppressWarnings("unchecked")
   private FileResource<?> createResource(String fullPath)
   {
      ResourceFactory resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
               .get();
      return resourceFactory.create(FileResource.class, new File(fullPath));
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name(COMMAND_NAME).description(COMMAND_DESCRIPTION)
               .category(Categories.create(COMMAND_CATEGORY));
   }

}
