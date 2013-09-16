/**
 * 
 */
package org.jboss.forge.addon.scaffold.faces;

import java.util.List;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.scaffold.spi.ScaffoldContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * The scaffold provider for JSF 2.0
 */
public class FacesScaffoldProvider extends AbstractFacet<Project> implements ScaffoldProvider
{

   @Override
   public boolean install()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isInstalled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public String getName()
   {
      return "Faces";
   }

   @Override
   public String getDescription()
   {
      return "Scaffold a Faces project from JPA entities";
   }

   @Override
   public List<Resource<?>> setup(ScaffoldContext scaffoldContext)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Resource<?>> generateFrom(Iterable<Resource<?>> resources, ScaffoldContext scaffoldContext)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean needsOverwriteConfirmation(ScaffoldContext scaffoldContext)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
