/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ejb.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Setups EJB in a {@link Project}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(DependencyFacet.class)
public class EJBSetupWizard extends AbstractJavaEECommand
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("EJB: Setup")
               .description("Setup EJB in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "EJB"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "EJB Version")
   private UISelectOne<EJBFacet> version;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      version.setItemLabelConverter(new Converter<EJBFacet, String>()
      {
         @Override
         public String convert(EJBFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (EJBFacet choice : version.getValueChoices())
      {
         if (version.getValue() == null || choice.getSpecVersion().compareTo(version.getValue().getSpecVersion()) >= 1)
         {
            version.setDefaultValue(choice);
         }
      }

      builder.add(version);
   }

   @Override
   public Result execute(final UIContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), version.getValue()))
      {
         return Results.success("EJB has been installed.");
      }
      return Results.fail("Could not install EJB.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
