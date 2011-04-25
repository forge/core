package org.jboss.forge.shell.plugins.builtin.project;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.services.FacetFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;
import org.jboss.forge.shell.util.ConstraintInspector;

public class InstalledFacetsCompleter extends SimpleTokenCompleter
{
   @Inject
   private FacetFactory factory;

   @Inject
   private Shell shell;

   @Override
   public List<Object> getCompletionTokens()
   {
      List<Object> result = new ArrayList<Object>();

      Project project = shell.getCurrentProject();
      List<Facet> allFacets = factory.getFacets();
      for (Facet facet : allFacets)
      {
         if (project.hasFacet(facet.getClass()))
         {
            result.add(ConstraintInspector.getName(facet.getClass()));
         }
      }

      return result;
   }

}
