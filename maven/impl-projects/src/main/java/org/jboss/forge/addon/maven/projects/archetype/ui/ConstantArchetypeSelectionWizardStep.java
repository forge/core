/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

/**
 * Represents a WizardStep that can be used by a ProjectType which resolves to a single constant
 * archetype using maven coordinates.
 */
public abstract class ConstantArchetypeSelectionWizardStep extends AbstractArchetypeSelectionWizardStep
{
   private String archetypeRepository;
   private String archetypeGroupId;
   private String archetypeArtifactId;
   private String archetypeVersion;

   public ConstantArchetypeSelectionWizardStep()
   {
   }

   public ConstantArchetypeSelectionWizardStep(String archetypeGroupId, String archetypeArtifactId,
            String archetypeVersion)
   {
      this(archetypeGroupId, archetypeArtifactId, archetypeVersion, null);
   }

   public ConstantArchetypeSelectionWizardStep(String archetypeGroupId, String archetypeArtifactId,
            String archetypeVersion, String archetypeRepository)
   {
      this.archetypeGroupId = archetypeGroupId;
      this.archetypeArtifactId = archetypeArtifactId;
      this.archetypeVersion = archetypeVersion;
      this.archetypeRepository = archetypeRepository;
   }

   @Override
   protected String getArchetypeRepository()
   {
      return archetypeRepository;
   }

   @Override
   protected String getArchetypeVersion()
   {
      return archetypeVersion;
   }

   @Override
   protected String getArchetypeArtifactId()
   {
      return archetypeArtifactId;
   }

   @Override
   protected String getArchetypeGroupId()
   {
      return archetypeGroupId;
   }

   protected void setArchetypeArtifactId(String archetypeArtifactId)
   {
      this.archetypeArtifactId = archetypeArtifactId;
   }

   protected void setArchetypeGroupId(String archetypeGroupId)
   {
      this.archetypeGroupId = archetypeGroupId;
   }

   protected void setArchetypeRepository(String archetypeRepository)
   {
      this.archetypeRepository = archetypeRepository;
   }

   protected void setArchetypeVersion(String archetypeVersion)
   {
      this.archetypeVersion = archetypeVersion;
   }
}
