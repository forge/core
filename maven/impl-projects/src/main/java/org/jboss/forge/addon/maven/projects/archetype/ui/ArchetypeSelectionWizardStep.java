/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.archetype.ui;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
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
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
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
public class ArchetypeSelectionWizardStep extends AbstractUICommand implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Archetype Group Id", required = true)
   private UIInput<String> archetypeGroupId;

   @Inject
   @WithAttributes(label = "Archetype Artifact Id", required = true)
   private UIInput<String> archetypeArtifactId;

   @Inject
   @WithAttributes(label = "Archetype Version", required = true)
   private UIInput<String> archetypeVersion;

   @Inject
   @WithAttributes(label = "Archetype repository URL")
   private UIInput<String> archetypeRepository;

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
               .description("Enter a Maven archetype coordinate");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(archetypeGroupId).add(archetypeArtifactId).add(archetypeVersion).add(archetypeRepository);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      String repository = archetypeRepository.getValue();
      if (!Strings.isNullOrEmpty(repository) && !Strings.isURL(repository))
      {
         validator.addValidationError(archetypeRepository, "Archetype repository must be a valid URL");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = (Project) uiContext.getAttributeMap().get(Project.class);
      String coordinate = archetypeGroupId.getValue() + ":" + archetypeArtifactId.getValue() + ":"
               + archetypeVersion.getValue();
      DependencyQueryBuilder depQuery = DependencyQueryBuilder.create(coordinate);
      String repository = archetypeRepository.getValue();
      if (repository != null)
      {
         depQuery.setRepositories(new DependencyRepository("archetype", repository));
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
