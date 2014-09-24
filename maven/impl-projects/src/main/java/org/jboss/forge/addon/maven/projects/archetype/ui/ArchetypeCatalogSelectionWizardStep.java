/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.archetype.ui;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactory;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactoryRegistry;
import org.jboss.forge.addon.maven.projects.archetype.ArchetypeHelper;
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
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.util.Strings;

/**
 * Displays a list of archetypes to choose from
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeCatalogSelectionWizardStep extends AbstractUICommand implements UIWizardStep
{
   @Inject
   private ArchetypeCatalogFactoryRegistry archetypeRegistry;

   @Inject
   @WithAttributes(label = "Catalog", required = true)
   private UISelectOne<ArchetypeCatalogFactory> catalog;

   @Inject
   @WithAttributes(label = "Archetype", required = true)
   private UISelectOne<Archetype> archetype;

   @Inject
   private DependencyResolver resolver;

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
      // List of catalogs
      catalog.setItemLabelConverter(new Converter<ArchetypeCatalogFactory, String>()
      {
         @Override
         public String convert(ArchetypeCatalogFactory source)
         {
            return (source != null) ? source.getName() : null;
         }
      }).setValueChoices(archetypeRegistry.getArchetypeCatalogFactories());

      // List of Archetypes
      archetype.setItemLabelConverter(new Converter<Archetype, String>()
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
      Dependency resolvedArtifact = resolver.resolveArtifact(depQuery);
      FileResource<?> artifact = resolvedArtifact.getArtifact();
      MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
      File fileRoot = project.getRoot().reify(DirectoryResource.class).getUnderlyingResourceObject();
      ArchetypeHelper archetypeHelper = new ArchetypeHelper(artifact.getResourceInputStream(), fileRoot,
               metadataFacet.getProjectGroupName(), metadataFacet.getProjectName(), metadataFacet.getProjectVersion());
      JavaSourceFacet facet = (JavaSourceFacet) project.getFacet(JavaSourceFacet.class);
      archetypeHelper.setPackageName(facet.getBasePackage());
      archetypeHelper.execute();
      return Results.success();
   }
}
