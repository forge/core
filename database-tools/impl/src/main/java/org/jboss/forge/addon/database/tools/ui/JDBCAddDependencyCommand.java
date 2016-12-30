/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Completers;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Adds a dependency in the current project to the specified driver
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class JDBCAddDependencyCommand extends AbstractProjectCommand
{
   private UISelectOne<DatabaseType> dbType;
   private UIInput<String> version;
   private UISelectOne<String> scope;

   private DependencyResolver dependencyResolver;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("JDBC: Add Dependency")
               .description("Adds a dependency in the current project to the specified driver")
               .category(Categories.create("Database", "JDBC"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      dependencyResolver = SimpleContainer.getServices(getClass().getClassLoader(), DependencyResolver.class).get();
      InputComponentFactory factory = builder.getInputComponentFactory();

      dbType = factory.createSelectOne("dbType", DatabaseType.class).setLabel("Database Type")
               .setDescription("The database driver to be added to this project").setRequired(true)
               .setNote(() -> {
                  return (dbType.hasValue()) ? dbType.getValue().getDriverCoordinate().toString() : null;
               })
               .setValueChoices(DatabaseType.getTypesWithDriverSet());

      version = factory.createInput("version", String.class).setLabel("Driver Version")
               .setDescription("The JDBC driver version to be used")
               .setCompleter(new UICompleter<String>()
               {
                  @Override
                  public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                           String value)
                  {
                     return Completers.fromValues(getVersionsFor(dbType.getValue())).getCompletionProposals(context,
                              input, value);
                  }
               });

      scope = factory.createSelectOne("scope", String.class).setLabel("Dependency Scope")
               .setDescription("The scope this database driver dependency should use when added to this project")
               .setRequired(true)
               .setDefaultValue("runtime")
               .setValueChoices(Arrays.asList("compile", "provided", "runtime", "test"));

      builder.add(dbType).add(version).add(scope);
   }

   private List<String> getVersionsFor(DatabaseType type)
   {
      if (type == null)
      {
         return Collections.emptyList();
      }
      else
      {
         return dependencyResolver
                  .resolveVersions(DependencyQueryBuilder.create(type.getDriverCoordinate()))
                  .stream().map(Coordinate::getVersion).collect(Collectors.toList());
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      DatabaseType chosenDbType = dbType.getValue();
      String chosenScope = scope.getValue();
      String chosenVersion = version.getValue();
      if (chosenVersion == null)
      {
         List<String> versions = getVersionsFor(chosenDbType);
         if (!versions.isEmpty())
         {
            chosenVersion = versions.get(versions.size() - 1);
         }
      }
      if (chosenVersion == null)
      {
         return Results.fail("No version specified or found for " + chosenDbType.getDriverCoordinate());
      }
      Project project = getSelectedProject(context);

      DependencyBuilder dependency = DependencyBuilder.create()
               .setCoordinate(chosenDbType.getDriverCoordinate())
               .setVersion(chosenVersion)
               .setScopeType(chosenScope);

      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      dependencyFacet.addDirectDependency(dependency);

      return Results.success("JDBC Driver Dependency " + dependency + " installed");
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
