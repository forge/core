/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactory;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactoryRegistry;
import org.jboss.forge.addon.maven.archetype.ArchetypeHelper;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;

/**
 * Displays a list of archetypes to choose from
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeCatalogSelectionWizardStep extends AbstractUICommand implements UIWizardStep
{
   private UISelectOne<ArchetypeCatalogFactory> catalog;
   private UISelectOne<Archetype> archetype;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Maven: Choose Archetype")
               .description("Choose a Maven archetype for your project");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      ArchetypeCatalogFactoryRegistry archetypeRegistry = SimpleContainer
               .getServices(getClass().getClassLoader(), ArchetypeCatalogFactoryRegistry.class).get();
      InputComponentFactory factory = builder.getInputComponentFactory();
      // List of catalogs
      catalog = factory.createSelectOne("catalog", ArchetypeCatalogFactory.class)
               .setLabel("Catalog")
               .setRequired(true)
               .setItemLabelConverter(new Converter<ArchetypeCatalogFactory, String>()
               {
                  @Override
                  public String convert(ArchetypeCatalogFactory source)
                  {
                     return (source != null) ? source.getName() : null;
                  }
               }).setValueChoices(archetypeRegistry.getArchetypeCatalogFactories());

      // List of Archetypes
      archetype = factory.createSelectOne("archetype", Archetype.class)
               .setLabel("Archetype")
               .setRequired(true).setItemLabelConverter(new Converter<Archetype, String>()
               {
                  @Override
                  public String convert(Archetype source)
                  {
                     if (source == null)
                     {
                        return null;
                     }
                     return source.getGroupId() + ":" + source.getArtifactId() + ":" + source.getVersion();
                  }
               }).setValueChoices(new Callable<Iterable<Archetype>>()
               {
                  @Override
                  public Iterable<Archetype> call() throws Exception
                  {
                     Set<Archetype> result = new LinkedHashSet<>();
                     if (catalog.hasValue())
                     {
                        ArchetypeCatalogFactory catalogFactory = catalog.getValue();
                        ArchetypeCatalog archetypes = catalogFactory.getArchetypeCatalog();
                        if (archetypes != null)
                        {
                           result.addAll(archetypes.getArchetypes());
                        }
                     }
                     return result;
                  }
               }).setDescription(new Callable<String>()
               {
                  @Override
                  public String call() throws Exception
                  {
                     Archetype value = archetype.getValue();
                     return value == null ? null : value.getDescription();
                  }
               });
      builder.add(catalog).add(archetype);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = (Project) uiContext.getAttributeMap().get(Project.class);
      Archetype chosenArchetype = archetype.getValue();
      String coordinate = chosenArchetype.getGroupId() + ":" + chosenArchetype.getArtifactId() + ":"
               + chosenArchetype.getVersion();
      DependencyQueryBuilder depQuery = DependencyQueryBuilder.create(coordinate);
      String repository = chosenArchetype.getRepository();
      if (!Strings.isNullOrEmpty(repository))
      {
         if (repository.endsWith(".xml"))
         {
            int lastRepositoryPath = repository.lastIndexOf('/');
            if (lastRepositoryPath > -1)
               repository = repository.substring(0, lastRepositoryPath);
         }
         if (!repository.isEmpty())
         {
            depQuery.setRepositories(new DependencyRepository("archetype", repository));
         }
      }
      DependencyResolver resolver = SimpleContainer.getServices(getClass().getClassLoader(), DependencyResolver.class)
               .get();
      Dependency resolvedArtifact = resolver.resolveArtifact(depQuery);
      FileResource<?> artifact = resolvedArtifact.getArtifact();
      MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
      File fileRoot = project.getRoot().reify(DirectoryResource.class).getUnderlyingResourceObject();
      ArchetypeHelper archetypeHelper = new ArchetypeHelper(artifact.getResourceInputStream(), fileRoot,
               metadataFacet.getProjectGroupName(), metadataFacet.getProjectName(), metadataFacet.getProjectVersion());
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      archetypeHelper.setPackageName(facet.getBasePackage());
      archetypeHelper.execute();
      return Results.success();
   }
}
