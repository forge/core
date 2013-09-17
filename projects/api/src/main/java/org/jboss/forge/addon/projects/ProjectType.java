package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * Provides additional project configuration for use during new project creation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectType
{
   /**
    * Return the human-readable project type. This should be relatively unique.
    */
   public String getType();

   /**
    * Return the {@link UIWizardStep} {@link Class} that begins project configuration of this type.
    */
   public Class<? extends UIWizardStep> getSetupFlow();

   /**
    * Return all {@link Facet} {@link Class} types required by a project of this type.
    */
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets();
}
