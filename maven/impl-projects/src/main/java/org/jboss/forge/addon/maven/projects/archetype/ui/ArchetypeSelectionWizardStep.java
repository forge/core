/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;

/**
 * Displays a list of archetypes to choose from
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeSelectionWizardStep extends AbstractArchetypeSelectionWizardStep
{
   private UIInput<String> archetypeGroupId;
   private UIInput<String> archetypeArtifactId;
   private UIInput<String> archetypeVersion;
   private UIInput<String> archetypeRepository;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Maven: Choose Archetype")
               .description("Enter a Maven archetype coordinate");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory inputFactory = builder.getInputComponentFactory();
      archetypeGroupId = inputFactory.createInput("archetypeGroupId", String.class).setLabel("Archetype Group Id")
               .setRequired(true);
      archetypeArtifactId = inputFactory.createInput("archetypeArtifactId", String.class)
               .setLabel("Archetype Artifact Id")
               .setRequired(true);
      archetypeVersion = inputFactory.createInput("archetypeVersion", String.class)
               .setLabel("Archetype Version")
               .setRequired(true);
      archetypeRepository = inputFactory.createInput("archetypeRepository", String.class)
               .setLabel("Archetype repository URL");
      builder.add(archetypeGroupId).add(archetypeArtifactId).add(archetypeVersion).add(archetypeRepository);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      String repository = getArchetypeRepository();
      if (!Strings.isNullOrEmpty(repository) && !Strings.isURL(repository))
      {
         validator.addValidationError(archetypeRepository, "Archetype repository must be a valid URL");
      }
   }

   @Override
   protected String getArchetypeRepository()
   {
      return archetypeRepository.getValue();
   }

   @Override
   protected String getArchetypeVersion()
   {
      return archetypeVersion.getValue();
   }

   @Override
   protected String getArchetypeArtifactId()
   {
      return archetypeArtifactId.getValue();
   }

   @Override
   protected String getArchetypeGroupId()
   {
      return archetypeGroupId.getValue();
   }
}
