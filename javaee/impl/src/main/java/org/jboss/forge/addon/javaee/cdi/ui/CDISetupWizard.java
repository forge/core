/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ DependencyFacet.class, ResourcesFacet.class, WebResourcesFacet.class })
public class CDISetupWizard extends AbstractJavaEECommand
{
   @Override
   public Metadata getMetadata()
   {
      return Metadata.from(super.getMetadata(), getClass())
               .name("CDI: Setup")
               .description("Setup CDI in your project")
               .category(Categories.create(super.getMetadata().getCategory(), "CDI"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "CDI Version")
   private UISelectOne<CDIFacet> choices;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      choices.setItemLabelConverter(new Converter<CDIFacet, String>()
      {
         @Override
         public String convert(CDIFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (CDIFacet cdi : choices.getValueChoices())
      {
         if (choices.getValue() == null || cdi.getSpecVersion().compareTo(choices.getValue().getSpecVersion()) >= 1)
         {
            choices.setDefaultValue(cdi);
         }
      }

      builder.add(choices);
   }

   @Override
   public Result execute(final UIContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), choices.getValue()))
      {
         return Results.success("CDI has been installed.");
      }
      return Results.fail("Could not install CDI.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
