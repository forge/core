package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * Provides additional project configuration for use during new project creation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectType
{
   /**
    * Return the human-readable name for this {@link ProjectType}. This should be relatively unique.
    */
   public String getType();

   /**
    * Return the {@link UIWizardStep} {@link Class} that begins {@link Project} configuration of this
    * {@link ProjectType}.
    */
   public Class<? extends UIWizardStep> getSetupFlow();

   /**
    * Return all {@link ProjectFacet} {@link Class} types required by a {@link Project} of this {@link ProjectType}.
    */
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets();

   /**
    * Defines the priority of this {@link ProjectType}. Lower values receive a higher priority.
    */
   public int priority();
}
